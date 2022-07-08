const http = require('http')
const fs = require('fs')

let url = 'http://www.esk365.com/tools/gnhb/hblist.asp?id='

let ids = JSON.parse(fs.readFileSync('start_id.json').toString('utf-8'))

function getFlight(index) {
    console.log(index)
    let req = http.request(url + ids[index], res => {
        let chunks = []
        res.on('data', chunk => {
            chunks.push(chunk)
        })
        res.on('end', () => {
            let html = Buffer.concat(chunks).toString('utf-8')
            let reg = /lm_t lq">[\s\S]*lm_t lq/
            let reg2 = /[\u4e00-\u9fa5]{2,10}-[\u4e00-\u9fa5]{2,10}/g
            let reg3 = /id=[\w\d]{1,10}/g
            flight_name = html.match(reg)[0].match(reg2)
            flight_name = flight_name || []
            flight_id = html.match(reg)[0].match(reg3)
            flight_id = flight_id ? flight_id.map(item => item.split('=')[1]) : []

            if (index) {
                let old_name = JSON.parse(fs.readFileSync('flight_name.json').toString('utf-8'))
                let old_id = JSON.parse(fs.readFileSync('flight_id.json').toString('utf-8'))

                flight_name = [...old_name, ...flight_name]
                flight_id = [...old_id, ...flight_id]
            }

            fs.writeFileSync('flight_name.json', JSON.stringify(flight_name), err => {
                console.log('writeFileErr:' + err)
            })
            fs.writeFileSync('flight_id.json', JSON.stringify(flight_id), err => {
                console.log('writeFileErr:' + err)
            })

            if (index < ids.length - 1) getFlight(index + 1)
        })
    })

    req.end()
}

getFlight(170)
