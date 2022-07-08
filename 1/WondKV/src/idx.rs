use crate::utils::time;
use crate::wondkv::WondKV;
use crate::storage::entry;

pub enum DataType {
    String,
    List,
    Hash,
    Set,
    ZSet,
}

pub enum AHashOperation {
    HashHSet,
    HashHDel,
    HashHClear,
    HashHExpire,
}

impl Into<u16> for AHashOperation {
    fn into(self) -> u16 {
        match self {
            AHashOperation::HashHSet => 0,
            AHashOperation::HashHDel => 1,
            AHashOperation::HashHClear => 2,
            AHashOperation::HashHExpire => 3,
        }
    }
}

impl From<u16> for AHashOperation {
    fn from(kind: u16) -> Self {
        match kind {
            0 => AHashOperation::HashHSet,
            1 => AHashOperation::HashHDel,
            2 => AHashOperation::HashHClear,
            3 => AHashOperation::HashHExpire,
            _ => panic!(),
        }
    }
}

impl WondKV {
    pub fn build_hash_index(&mut self, entry: entry::Entry) {
        if !entry.valid {
            return;
        }
        let key = String::from_utf8(entry.meta.key.clone()).ok().unwrap();
        match AHashOperation::from(entry.get_mark()) {
            AHashOperation::HashHSet => self.hash_index.indexes.hset(key, String::from_utf8(entry.meta.extra.clone()).ok().unwrap(), entry.meta.value),
            AHashOperation::HashHDel => self.hash_index.indexes.hdel(key, String::from_utf8(entry.meta.extra.clone()).ok().unwrap()),
            AHashOperation::HashHClear => self.hash_index.indexes.hclear(key),
            AHashOperation::HashHExpire => {
                if entry.time_stamp < time::time_now() {
                    self.hash_index.indexes.hclear(key);
                } else {
                    if self.expires.contains_key(&key) {
                        *self.expires.get_mut(&key).unwrap() = entry.time_stamp as u32;
                    } else {
                        self.expires.insert(key, entry.time_stamp as u32);
                    }
                }
                true
            },
        };
    }

    pub fn load_idx_from_files(&mut self) {
        if self.arch_files.len() == 0 && self.active_file.is_none() {
            return;
        }
        let mut all_file_id = vec![];
        for id in self.arch_files.keys() {
            all_file_id.push(*id);
        }
        all_file_id.sort();
        for id in all_file_id {
            let file = self.arch_files.get(&id).unwrap().clone();
            let mut offset = 0;
            loop {
                let entry = file.borrow().read(offset);
                if entry.is_none() {
                    break;
                }
                let entry = entry.unwrap();
                offset += entry.size();
                if entry.meta.key.len() > 0 {
                    self.build_index(entry);
                }
            }
        }
        let file = self.active_file.as_ref().unwrap().clone();
        let mut offset = 0;
        loop {
            let entry = file.borrow().read(offset);
            if entry.is_none() {
                break;
            }
            let entry = entry.unwrap();
            offset += entry.size();
            if entry.meta.key.len() > 0 {
                self.build_index(entry);
            }
        }
    }
}

