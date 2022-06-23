#![no_std]
#![forbid(unsafe_code)]

pub use crc_catalog::*;

pub struct Crc<W: Width> {
    pub algorithm: &'static Algorithm<W>,
    table: [W; 256],
}

#[derive(Clone)]
pub struct Digest<'a, W: Width> {
    crc: &'a Crc<W>,
    value: W,
}

impl Crc<u32> {
    pub const fn new(algorithm: &'static Algorithm<u32>) -> Self {
        let table = crc32_table(algorithm.width, algorithm.poly, algorithm.refin);
        Self { algorithm, table }
    }

    pub const fn checksum(&self, bytes: &[u8]) -> u32 {
        let mut crc = self.init(self.algorithm.init);
        crc = self.update(crc, bytes);
        self.finalize(crc)
    }

    const fn init(&self, initial: u32) -> u32 {
        if self.algorithm.refin {
            initial.reverse_bits() >> (32u8 - self.algorithm.width)
        } else {
            initial << (32u8 - self.algorithm.width)
        }
    }

    const fn table_entry(&self, index: u32) -> u32 {
        self.table[(index & 0xFF) as usize]
    }

    const fn update(&self, mut crc: u32, bytes: &[u8]) -> u32 {
        let mut i = 0;
        if self.algorithm.refin {
            while i < bytes.len() {
                let table_index = crc ^ bytes[i] as u32;
                crc = self.table_entry(table_index) ^ (crc >> 8);
                i += 1;
            }
        } else {
            while i < bytes.len() {
                let table_index = (crc >> 24) ^ bytes[i] as u32;
                crc = self.table_entry(table_index) ^ (crc << 8);
                i += 1;
            }
        }
        crc
    }

    const fn finalize(&self, mut crc: u32) -> u32 {
        if self.algorithm.refin ^ self.algorithm.refout {
            crc = crc.reverse_bits();
        }
        if !self.algorithm.refout {
            crc >>= 32u8 - self.algorithm.width;
        }
        crc ^ self.algorithm.xorout
    }

    pub const fn digest(&self) -> Digest<u32> {
        self.digest_with_initial(self.algorithm.init)
    }

    /// Construct a `Digest` with a given initial value.
    ///
    /// This overrides the initial value specified by the algorithm.
    /// The effects of the algorithm's properties `refin` and `width`
    /// are applied to the custom initial value.
    pub const fn digest_with_initial(&self, initial: u32) -> Digest<u32> {
        let value = self.init(initial);
        Digest::new(self, value)
    }
}

impl<'a> Digest<'a, u32> {
    const fn new(crc: &'a Crc<u32>, value: u32) -> Self {
        Digest { crc, value }
    }

    pub fn update(&mut self, bytes: &[u8]) {
        self.value = self.crc.update(self.value, bytes);
    }

    pub const fn finalize(self) -> u32 {
        self.crc.finalize(self.value)
    }
}

pub(crate) const fn crc32(poly: u32, reflect: bool, mut value: u32) -> u32 {
    if reflect {
        let mut i = 0;
        while i < 8 {
            value = (value >> 1) ^ ((value & 1) * poly);
            i += 1;
        }
    } else {
        value <<= 24;

        let mut i = 0;
        while i < 8 {
            value = (value << 1) ^ (((value >> 31) & 1) * poly);
            i += 1;
        }
    }
    value
}

pub(crate) const fn crc32_table(width: u8, poly: u32, reflect: bool) -> [u32; 256] {
    let poly = if reflect {
        let poly = poly.reverse_bits();
        poly >> (32u8 - width)
    } else {
        poly << (32u8 - width)
    };

    let mut table = [0u32; 256];
    let mut i = 0;
    while i < table.len() {
        table[i] = crc32(poly, reflect, i as u32);
        i += 1;
    }
    table
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn basics() {
        let crc = Crc::<u32>::new(&CRC_32_ISCSI);
        assert_eq!(crc.checksum(b"123456789"), 0xe3069283);
        let mut digest = crc.digest();
        digest.update(b"123456789");
        assert_eq!(digest.finalize(), 0xe3069283);
    }
}