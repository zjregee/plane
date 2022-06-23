#[cfg(test)]
mod tests {
    use std::env;
    use std::fs::{self, File};
    use crate::storage::entry;
    use crate::storage::db_file;

    #[test]
    fn basics() {
        let current_path = env::current_dir().ok().unwrap();
        let temp_dir = current_path.join("tmp_db_file_test");
        fs::create_dir(temp_dir.clone()).ok();
        let path = temp_dir.clone().into_os_string().into_string().ok().unwrap();
        let ret = db_file::build(path.clone());
        assert_eq!(ret.is_some(), true);
        assert_eq!(ret.as_ref().unwrap().0.len(), 0);
        assert_eq!(ret.as_ref().unwrap().1, 0);
        File::create(temp_dir.join("1.data.hash")).ok();
        File::create(temp_dir.join("2.data.hash")).ok();
        File::create(temp_dir.join("3.data.hash")).ok();
        let ret = db_file::build(path.clone());
        assert_eq!(ret.is_some(), true);
        assert_eq!(ret.as_ref().unwrap().0.len(), 2);
        assert_eq!(ret.as_ref().unwrap().1, 3);
        let mut file = db_file::DBFile::new(path, 1).unwrap();
        file.write(entry::Entry::new(vec![1, 2, 3], vec![1, 2], vec![1], 0, 0));
        file.write(entry::Entry::new(vec![1, 2, 3], vec![1, 2], vec![1], 0, 1));
        file.write(entry::Entry::new(vec![1, 2, 3], vec![1, 2], vec![1], 0, 2));
        file.sync();
        let mut offset = 0;
        let entry = file.read(offset).unwrap();
        assert_eq!(entry.meta.key, vec![1, 2, 3]);
        assert_eq!(entry.meta.value, vec![1, 2]);
        assert_eq!(entry.meta.extra, vec![1]);
        assert_eq!(entry.get_mark(), 0);
        offset += entry.size();
        let entry = file.read(offset).unwrap();
        assert_eq!(entry.meta.key, vec![1, 2, 3]);
        assert_eq!(entry.meta.value, vec![1, 2]);
        assert_eq!(entry.meta.extra, vec![1]);
        assert_eq!(entry.get_mark(), 1);
        offset += entry.size();
        let entry = file.read(offset).unwrap();
        assert_eq!(entry.meta.key, vec![1, 2, 3]);
        assert_eq!(entry.meta.value, vec![1, 2]);
        assert_eq!(entry.meta.extra, vec![1]);
        assert_eq!(entry.get_mark(), 2);
        offset += entry.size();
        assert_eq!(file.read(offset).is_none(), true);
        file.close(false);
        fs::remove_dir_all(temp_dir).ok();
    }
}