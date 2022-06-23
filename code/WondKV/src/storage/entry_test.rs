#[cfg(test)]
mod tests {
    use crate::storage::entry;

    #[test]
    fn basics() {
        let mut entry = entry::Entry::new(vec![1, 2, 3], vec![3, 4], vec![1, 2], 0, 1);
        assert_eq!(entry.size(), 33);
        assert_eq!(entry.get_type(), 0);
        assert_eq!(entry.get_mark(), 1);
        let data = entry.encode().unwrap();
        assert_eq!(data[26], 1);
        assert_eq!(data[27], 2);
        assert_eq!(data[28], 3);
        assert_eq!(data[29], 3);
        assert_eq!(data[30], 4);
        assert_eq!(data[31], 1);
        assert_eq!(data[32], 2);
        let entry = entry::Entry::decode(data);
        assert_eq!(entry.valid, false);
        assert_eq!(entry.state, 1);
        let entry = entry::Entry::new_no_extra(vec![1, 2, 3], vec![1, 2], 0, 2);
        assert_eq!(entry.size(), 31);
        assert_eq!(entry.get_type(), 0);
        assert_eq!(entry.get_mark(), 2);
        assert_eq!(entry.meta.key, vec![1, 2, 3]);
        assert_eq!(entry.meta.value, vec![1, 2]);
        assert_eq!(entry.meta.extra, vec![]);
        let entry = entry::Entry::new_with_expire(vec![1, 2, 3], vec![1, 2], 1000, 0, 2);
        assert_eq!(entry.time_stamp, 1000);
    }
}