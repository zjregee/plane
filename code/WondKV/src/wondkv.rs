use std::fs::File;
use std::path::Path;
use std::sync::Arc;
use std::cell::RefCell;
use std::collections::HashMap;
use crate::config;
use crate::idx;
use crate::db_hash;
use crate::storage::db_file;
use crate::utils::time;
use crate::storage::entry;

pub struct WondKV {
    pub config: config::Config,
    pub active_file: Option<Arc<RefCell<db_file::DBFile>>>,
    pub arch_files: HashMap<u32, Arc<RefCell<db_file::DBFile>>>,
    pub hash_index: db_hash::HashIdx,
    pub expires: HashMap<String, u32>,
    pub closed: u32,
}

impl WondKV {
    pub fn close(&mut self) -> bool {
        let ret = self.active_file.as_ref().unwrap().borrow_mut().close(true);
        if !ret {
            return false;
        }
        for file in self.arch_files.values() {
            let ret = file.borrow_mut().close(false);
            if !ret {
                return false;
            }
        }
        self.closed = 1;
        true
    }

    pub fn is_closed(&self) -> bool {
        self.closed == 1
    }

    pub fn sync(&mut self) -> bool {
        if self.active_file.is_none() {
            return true;
        }
        self.active_file.as_ref().unwrap().borrow_mut().sync()
    }
}

impl WondKV {
    pub fn build_index(&mut self, entry: entry::Entry) {
        self.build_hash_index(entry);
    }

    pub fn store(&mut self, entry: entry::Entry) -> bool {
        if self.active_file.is_none() {
            let file_path = Path::new(&self.config.dir_path).join(format!("{}.data.hash", 1));
            let ret = File::create(file_path);
            if ret.is_err() {
                return false;
            }
            let new_file = db_file::DBFile::new(self.config.dir_path.clone(), 1);
            if new_file.is_none() {
                return false;
            }
            self.active_file = Some(Arc::new(RefCell::new(new_file.unwrap())));
        }
        if self.active_file.as_ref().unwrap().borrow().offset > self.config.max_file_size {
            let ret = self.active_file.as_ref().unwrap().borrow_mut().sync();
            if !ret {
                return false;
            }
            let new_file_id = self.active_file.as_ref().unwrap().borrow().id + 1;
            let file_path = Path::new(&self.config.dir_path).join(format!("{}.data.hash", new_file_id));
            let ret = File::create(file_path);
            if ret.is_err() {
                return false;
            }
            let new_file = db_file::DBFile::new(self.config.dir_path.clone(), new_file_id);
            if new_file.is_none() {
                return false;
            }
            self.arch_files.insert(new_file_id - 1, self.active_file.as_ref().unwrap().clone());
            self.active_file = Some(Arc::new(RefCell::new(new_file.unwrap())));
        }
        let ret = self.active_file.as_ref().unwrap().borrow_mut().write(entry);
        if !ret {
            return false;
        }
        true
    }

    pub fn check_entry(&mut self, entry: entry::Entry) -> bool {
        if !entry.valid {
            return false;
        }
        let mark = entry.get_mark();
        if mark == idx::HashOperation::HashHExpire.into() {
            if !self.expires.contains_key(&String::from_utf8(entry.meta.key.clone()).ok().unwrap()) {
                return false;
            }
            let deadline = *self.expires.get(&String::from_utf8(entry.meta.key.clone()).ok().unwrap()).unwrap();
            if deadline as u64 <= time::time_now() {
                return false;
            }
        }
        if mark == idx::HashOperation::HashHSet.into() {
            let val = self.hget(entry.meta.key, entry.meta.extra);
            if val.is_none() {
                return false;
            }
            if val.unwrap() != entry.meta.value {
                return false;
            }
        }
        true
    }

    pub fn check_expired(&mut self, key: Vec<u8>) -> bool {
        if !self.expires.contains_key(&String::from_utf8(key.clone()).ok().unwrap()) {
            return false;
        }
        let deadline = *self.expires.get(&String::from_utf8(key.clone()).ok().unwrap()).unwrap();
        if time::time_now() > deadline as u64 {
            let entry = entry::Entry::new_no_extra(key.clone(), vec![], 0, idx::HashOperation::HashHClear.into());
            self.hash_index.indexes.hclear(String::from_utf8(key.clone()).ok().unwrap());
            self.store(entry);
            self.expires.remove(&String::from_utf8(key.clone()).ok().unwrap());
            return true;
        }
        false
    }
}

impl WondKV {
    pub fn check_key_value(key: &Vec<u8>, value: &Vec<u8>) -> bool {
        if key.len() == 0 {
            return false;
        }
        if key.len() > 100 {
            return false;
        }
        if value.len() > 100 {
            return false;
        }
        true
    }
}