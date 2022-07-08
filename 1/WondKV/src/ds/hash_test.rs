#[cfg(test)]
mod tests {
    use crate::ds::hash;

    #[test]
    fn basics() {
        let mut hash = hash::Hash::new();
        hash.hset("key".to_string(), "a".to_string(), vec![1, 2, 3]);
        hash.hset("key".to_string(), "b".to_string(), vec![1, 2]);
        assert_eq!(hash.hget("key".to_string(), "a".to_string()).unwrap(), vec![1, 2, 3]);
        assert_eq!(hash.hget("key".to_string(), "b".to_string()).unwrap(), vec![1, 2]);
        hash.hdel("key".to_string(), "a".to_string());
        assert_eq!(hash.hget("key".to_string(), "a".to_string()), None);
    }
}