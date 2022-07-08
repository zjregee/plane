use std::cell::RefCell;
use std::collections::HashMap;
use std::sync::Arc;
use std::env;
use std::fs;
use crate::db_hash;
use crate::wondkv::WondKV;
use crate::storage::db_file;

pub fn default_config() -> Config {
    let current_path = env::current_dir().ok().unwrap();
    let temp_dir = current_path.join("data");
    Config {
        dir_path: temp_dir.to_str().unwrap().to_string(),
        max_file_size: 16 * 1024 * 1024,
    }
}

#[derive(Default)]
pub struct Config {
    pub dir_path: String,
    pub max_file_size: u32,
}

impl Config {
    pub fn open(&self) -> Option<WondKV> {
        fs::create_dir(self.dir_path.clone()).ok();
        let res = db_file::build(self.dir_path.clone());
        if res.is_none() {
            return None;
        }
        let res = res.unwrap();
        let active_file;
        if res.1 == 0 {
            active_file = None;
        } else {
            let file = db_file::DBFile::new(self.dir_path.clone(), res.1);
            if file.is_none() {
                active_file = None;
            } else {
                active_file = Some(Arc::new(RefCell::new(file.unwrap())));
            }
        }
        let files = res.0;
        let mut arch_files = HashMap::new();
        for entry in files.into_iter() {
            arch_files.insert(entry.0, Arc::new(RefCell::new(entry.1)));
        }
        let mut db = WondKV {
            active_file,
            arch_files,
            hash_index: db_hash::HashIdx::new(),
            expires: HashMap::new(),
            closed: 0,
            config: self.dup(),
        };
        db.load_idx_from_files();
        Some(db)
    }

    pub fn dup(&self) -> Config {
        Config {
            dir_path: self.dir_path.clone(),
            max_file_size: self.max_file_size,
        }
    }
}