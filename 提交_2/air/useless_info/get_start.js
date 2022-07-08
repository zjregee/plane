const http = require('http')
const fs = require('fs')

let url = 'http://www.esk365.com/tools/gnhb/'

let req = http.request(url, res => {
    let chunks = []
    res.on('data', chunk => {
        chunks.push(chunk)
    })
    res.on('end', () => {
        let html = Buffer.concat(chunks).toString('utf-8')
        let reg = /fa_wk3.*<\/a>[\s]*<\/div>/
        let reg2 = /[\u4e00-\u9fa5]{2,10}/g
        let reg3 = /id=[\d]{1,3}/g
        let start_name = html.match(reg)[0].match(reg2)
        let start_id = html.match(reg)[0].match(reg3)
        start_id = start_id.map(item => item.split('=')[1])
        console.log(start_id.length)
        fs.writeFile('start_name.json', JSON.stringify(start_name), err => {
            console.log('writeFileErr:' + err)
        })
        fs.writeFile('start_id.json', JSON.stringify(start_id), err => {
            console.log('writeFileErr:' + err)
        })
    })
})

req.end()
