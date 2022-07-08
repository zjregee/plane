#[cfg(test)]
mod tests {
    use std::env;
    use std::fs::{self, File};
    use crate::config;

    fn test_init() {
        let current_path = env::current_dir().ok().unwrap();
        let temp_dir = current_path.join("tmp_wondkv_test");
        fs::create_dir(temp_dir.clone()).ok();
        File::create(temp_dir.join("1.data")).ok();
        File::create(temp_dir.join("2.data")).ok();
        File::create(temp_dir.join("3.data")).ok();
    }

    fn test_drop() {
        let current_path = env::current_dir().ok().unwrap();
        let temp_dir = current_path.join("tmp_wondkv_test");
        fs::remove_dir_all(temp_dir).ok();
    }

    #[test]
    fn test_basics() {
        test_init();
        let mut config = config::default_config();
        let current_path = env::current_dir().ok().unwrap();
        let temp_dir = current_path.join("tmp_wondkv_test");
        config.dir_path = temp_dir.to_str().unwrap().to_string();
        let ret = config.open();
        assert_eq!(ret.is_none(), false);
        let mut db = ret.unwrap();
        db.hset(vec![1, 2, 3, 4], vec![4, 5], vec![6, 7, 8, 9]);
        db.hset(vec![3, 4], vec![4, 5], vec![6, 7, 8, 9]);
        assert_eq!(db.hget(vec![1, 2, 3, 4], vec![4, 5]).unwrap(), vec![6, 7, 8, 9]);
        assert_eq!(db.hget(vec![3, 4], vec![4, 5]).unwrap(), vec![6, 7, 8, 9]);
        test_drop()
    }
}