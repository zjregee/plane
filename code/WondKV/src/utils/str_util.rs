pub fn f64_to_string(val: f64) -> String {
    val.to_string()
}

pub fn string_to_f64(val: String) -> Option<f64> {
    val.parse::<f64>().ok()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_f64_to_string() {
        assert_eq!(f64_to_string(10.1), "10.1");
        assert_eq!(f64_to_string(2134.0), "2134");
    }

    #[test]
    fn test_string_to_f64() {
        assert_eq!(string_to_f64("10a".to_string()), None);
        assert_eq!(string_to_f64("10.1".to_string()), Some(10.1));
        assert_eq!(string_to_f64("2134".to_string()), Some(2134.0));
    }
}