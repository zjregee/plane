mod ds;
mod idx;
mod utils;
mod config;
mod wondkv;
mod storage;
mod db_hash;
mod wondkv_test;

use axum::{
    routing::post,
    Router,
    Json,
    Extension,
};
use std::env;
use std::net::SocketAddr;
use std::collections::HashMap;
use serde_json::{Value, json};
use serde::Deserialize;
use tokio::sync::mpsc;
use tokio::sync::oneshot;

struct Message {
    method: u8,
    key: Option<String>,
    value: Option<String>,
    channel: Option<oneshot::Sender<String>>,
}

impl Message {
    fn new() -> Message {
        Message {
            method: 0,
            key: None,
            value: None,
            channel: None,
        }
    }
}

struct SubMessage {
    method: u8,
    key: Option<String>,
    value: Option<String>,
    channel: Option<oneshot::Sender<String>>,
}

impl SubMessage {
    fn new() -> SubMessage {
        SubMessage {
            method: 0,
            key: None,
            value: None,
            channel: None,
        }
    }
}

#[derive(Clone)]
struct SubEntry {
    data: String,
    expired: u64,
}

#[derive(Deserialize)]
struct ReplyData {
    status: u8,
    data: String,
}

#[derive(Clone)]
struct State {
    address: String,
    port: u16,
    channel: mpsc::Sender<SubMessage>
}

#[tokio::main]
async fn main() {
    let args: Vec<String> = env::args().collect();

    if env::var("SERVER_TYPE").is_ok() && (env::var("SERVER_TYPE").ok().unwrap() == "sub" || env::var("SERVER_TYPE").ok().unwrap() == "SUB") {
        let port;
        if args.len() > 3 && args[3].parse::<u16>().is_ok() {
            port = args[3].parse::<u16>().unwrap();
        } else {
            port = 8080;
        }
        if args.len() < 3 {
            panic!()
        }
        let address = args[1].clone();
        let address_port;
        if args[2].parse::<u16>().is_ok() {
            address_port = args[2].parse::<u16>().unwrap();
        } else {
            panic!()
        }

        let (tx, mut rx) = mpsc::channel(32);
        let message = SubMessage::new();
        let _ = tx.send(message).await;
        let state = State {
            port: address_port, 
            address: address.clone(), 
            channel: tx,
        };
        tokio::spawn(async move {
            let app = Router::new()
                .route("/key/get", post(sub_get))
                .route("/key/set", post(sub_set))
                .layer(Extension(state));
            let addr = SocketAddr::from(([0, 0, 0, 0], port));
            println!("listening on {}", addr);
            axum::Server::bind(&addr)
                .serve(app.into_make_service())
                .await
                .unwrap();
        });

        let mut db = utils::lru_cache::LRUCache::<SubEntry>::new(32);
        while let Some(message) = rx.recv().await {
            match message.method {
                1 => {
                    let mut flag;
                    flag = db.contains_key(message.key.as_ref().unwrap().clone());
                    if flag {
                        let expired = db.get(message.key.as_ref().unwrap().clone()).unwrap().clone().expired;
                        if utils::time::time_now() > expired {
                            flag = false;
                            db.remove(message.key.as_ref().unwrap().clone());
                        }
                    }
                    if flag {
                        let _ = message.channel.unwrap().send(db.get(message.key.as_ref().unwrap().clone()).unwrap().clone().data);
                    } else {
                        let mut map = HashMap::new();
                        map.insert("key", message.key.clone().unwrap());
                        let client = reqwest::Client::new();
                        let url = format!("http://{}:{}/key/get", address, address_port);
                        let res = client.post(url)
                            .json(&map)
                            .send()
                            .await.ok().unwrap();
                        let data = res.json::<ReplyData>().await.ok().unwrap();
                        if data.status == 0 {
                            let time = utils::time::time_now() + 5;
                            db.put(message.key.unwrap(), SubEntry{ data: data.data.clone(), expired: time });
                            let _ = message.channel.unwrap().send(data.data);
                        } else {
                            let _ = message.channel.unwrap().send("".to_string());
                        }
                    }
                }
                2 => {
                    let time = utils::time::time_now() + 5;
                    db.put(message.key.unwrap(), SubEntry{ data: message.value.unwrap(), expired: time });
                }
                _ => ()
            }
        }
        return;
    }

    let port;
    if args.len() > 1 && args[1].parse::<u16>().is_ok() {
        port = args[1].parse::<u16>().unwrap();
    } else {
        port = 8080;
    }
    let (tx, mut rx) = mpsc::channel(32);
    let message = Message::new();
    let _ = tx.send(message).await;
    tokio::spawn(async move {
        let app = Router::new()
        .route("/key/get", post(kv_get))
        .route("/key/set", post(kv_set))
        .route("/key/test", post(kv_test))
        .layer(Extension(tx));
        let addr = SocketAddr::from(([0, 0, 0, 0], port));
        println!("listening on {}", addr);
        axum::Server::bind(&addr)
            .serve(app.into_make_service())
            .await
            .unwrap();
    });
    
    let config = config::default_config();
    let ret = config.open();
    if ret.is_none() {
        panic!();
    }
    let mut db = ret.unwrap();
    while let Some(message) = rx.recv().await {
        match message.method {
            1 => {
                if let Some(value) = db.hget(message.key.unwrap().into_bytes(), vec![1, 2, 3]) {
                    let _ = message.channel.unwrap().send(std::str::from_utf8(&value).unwrap().to_string());
                } else {
                    let _ = message.channel.unwrap().send("".to_string());
                }
            }
            2 => {
                db.hset(message.key.unwrap().into_bytes(), vec![1, 2, 3], message.value.unwrap().into_bytes());
            }
            _ => ()
        }
    }
}

async fn kv_get (
    Json(payload): Json<serde_json::Value>,
    Extension(state): Extension<mpsc::Sender<Message>>,
) -> Json<Value>  {
    let key = payload.as_object().unwrap().get("key").unwrap().as_str().unwrap().to_string();
    let (tx, rx) = oneshot::channel();
    let mut message = Message::new();
    message.method = 1;
    message.key = Some(key);
    message.channel = Some(tx);
    let _ = state.send(message).await;
    let value: String = rx.await.unwrap();
    if value == "" {
        Json(json!({ "status": 1, "data": value }))
    } else {
        Json(json!({ "status": 0, "data": value }))
    }
}

async fn kv_set (
    Json(payload): Json<serde_json::Value>,
    Extension(state): Extension<mpsc::Sender<Message>>,
) {
    let key = payload.as_object().unwrap().get("key").unwrap().as_str().unwrap().to_string();
    let value = payload.as_object().unwrap().get("value").unwrap().as_str().unwrap().to_string();
    let mut message = Message::new();
    message.method = 2;
    message.key = Some(key);
    message.value = Some(value);
    let _ = state.send(message).await;
}

async fn kv_test() {

}

async fn sub_get (
    Json(payload): Json<serde_json::Value>,
    Extension(state): Extension<State>,
) -> Json<Value> {
    let key = payload.as_object().unwrap().get("key").unwrap().as_str().unwrap().to_string();
    let (tx, rx) = oneshot::channel();
    let mut message = SubMessage::new();
    message.method = 1;
    message.key = Some(key);
    message.channel = Some(tx);
    let _ = state.channel.send(message).await;
    let value: String = rx.await.unwrap();
    if value == "" {
        Json(json!({ "status": 1, "data": value }))
    } else {
        Json(json!({ "status": 0, "data": value }))
    }
}

async fn sub_set (
    Json(payload): Json<serde_json::Value>,
    Extension(state): Extension<State>,
) {
    let key = payload.as_object().unwrap().get("key").unwrap().as_str().unwrap().to_string();
    let value = payload.as_object().unwrap().get("value").unwrap().as_str().unwrap().to_string();
    let mut message = SubMessage::new();
    message.method = 2;
    message.key = Some(key.clone());
    message.value = Some(value.clone());
    let _ = state.channel.send(message).await;
    let mut map = HashMap::new();
    map.insert("key", key);
    map.insert("value", value);
    let client = reqwest::Client::new();
    let url = format!("http://{}:{}/key/set", state.address, state.port);
    let _ = client.post(url)
        .json(&map)
        .send()
        .await;
}