use std::collections::HashMap;

#[derive(Default)]
pub struct Hash {
    record: HashMap<String, HashMap<String, Vec<u8>>>
}

impl Hash {
    pub fn new() -> Hash {
        Hash {
            record: HashMap::new(),
        }
    }

    pub fn hset(&mut self, key: String, field: String, value: Vec<u8>) -> bool {
        if !self.record.contains_key(&key) {
            self.record.insert(key.clone(), HashMap::new());
        }
        if self.record[&key].contains_key(&field) {
            *self.record.get_mut(&key).unwrap().get_mut(&field).unwrap() = value;
            false
        } else {
            self.record.get_mut(&key).unwrap().insert(field, value);
            true
        }
    }

    pub fn hset_nx(&mut self, key: String, field: String, value: Vec<u8>) -> bool {
        if !self.record.contains_key(&key) {
            self.record.insert(key.clone(), HashMap::new());
        }
        if !self.record[&key].contains_key(&field) {
            self.record.get_mut(&key).unwrap().insert(field, value);
            return true;
        }
        false
    }

    pub fn hget(&self, key: String, field: String) -> Option<Vec<u8>> {
        if self.record.contains_key(&key) {
            self.record.get(&key).unwrap().get(&field).cloned()
        } else {
            None
        }
    }

    pub fn hget_all(&self, key: String) -> Vec<(String, Vec<u8>)> {
        let mut res = vec![];
        if self.record.contains_key(&key) {
            for entry in self.record.get(&key).unwrap() {
                res.push((entry.0.to_owned(), entry.1.to_owned()));
            }
        }
        res
    }

    pub fn hdel(&mut self, key: String, field: String) -> bool {
        if !self.record.contains_key(&key) {
            return false;
        }
        if self.record.get(&key).unwrap().contains_key(&field) {
            self.record.get_mut(&key).unwrap().remove(&field);
            return true;
        }
        false
    }

    pub fn hkey_exists(&self, key: String) -> bool {
        self.record.contains_key(&key)
    }

    pub fn hexists(&self, key: String, field: String) -> bool {
        if !self.record.contains_key(&key) {
            return false;
        }
        self.record.get(&key).unwrap().contains_key(&field)
    }

    pub fn hlen(&self, key: String) -> u32 {
        if !self.record.contains_key(&key) {
            return 0;
        }
        self.record.get(&key).unwrap().len() as u32
    }

    pub fn hkeys(&self, key: String) -> Vec<String> {
        let mut res = vec![];
        if self.record.contains_key(&key) {
            for entry in self.record.get(&key).unwrap().keys() {
                res.push(entry.to_owned());
            }
        }
        res
    }

    pub fn hvals(&self, key: String) -> Vec<Vec<u8>> {
        let mut res = vec![];
        if self.record.contains_key(&key) {
            for entry in self.record.get(&key).unwrap().values() {
                res.push(entry.to_owned());
            }
        }
        res
    }

    pub fn hclear(&mut self, key: String) -> bool {
        if !self.record.contains_key(&key) {
            return false;
        }
        self.record.remove(&key);
        true
    }
}