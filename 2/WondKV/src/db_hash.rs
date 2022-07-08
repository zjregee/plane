use std::sync::Mutex;
use crate::ds::hash;
use crate::idx;
use crate::storage::entry;
use crate::wondkv::WondKV;

#[derive(Default)]
pub struct HashIdx {
    pub indexes: hash::Hash,
    pub lock: Mutex<bool>,
}

impl HashIdx {
    pub fn new() -> HashIdx {
        HashIdx {
            indexes: hash::Hash::new(),
            lock: Mutex::new(true),
        }
    }
}

impl WondKV {
    pub fn hset(&mut self, key: Vec<u8>, field: Vec<u8>, value: Vec<u8>) -> bool {
        let ret = WondKV::check_key_value(&key, &value);
        if !ret {
            return false;
        }
        let old_val = self.hget(key.clone(), field.clone());
        if old_val.is_some() {
            if old_val.unwrap() == value {
                return true;
            }
        }
        let _ = self.hash_index.lock.lock().unwrap();
        let entry = entry::Entry::new(key.clone(), value.clone(), field.clone(), 0, idx::AHashOperation::HashHSet.into());
        let ret = self.store(entry);
        if !ret {
            return false;
        }
        self.hash_index.indexes.hset(String::from_utf8(key).ok().unwrap(), String::from_utf8(field).ok().unwrap(), value);
        true
    }

    pub fn hget(&mut self, key: Vec<u8>, field: Vec<u8>) -> Option<Vec<u8>> {
        let ret = WondKV::check_key_value(&key, &vec![]);
        if !ret {
            return None;
        }
        let _ = self.hash_index.lock.lock().unwrap();
        if self.check_expired(key.clone()) {
            return None;
        }
        self.hash_index.indexes.hget(String::from_utf8(key).ok().unwrap(), String::from_utf8(field).ok().unwrap())
    }
}