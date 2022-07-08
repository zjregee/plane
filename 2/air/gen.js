const fs = require('fs')

let company = JSON.parse(fs.readFileSync('company.json').toString('utf-8'))
let time = JSON.parse(fs.readFileSync('time.json').toString('utf-8'))
let code = JSON.parse(fs.readFileSync('airport_code.json').toString('utf-8'))

let airports = Object.keys(code).slice(0, 20)

let price = [1500, 1000, 500]

function gen(airline, freq, month, day, callback) {
    // let f2 = JSON.parse(fs.readFileSync('ticket_fixed.json').toString('utf-8'))
    let f2 = []
    for (let i = 0; i < airline; i++) {
        let code1 = code[airports[Math.floor(Math.random() * airports.length)]]
        let code2 = code[airports[Math.floor(Math.random() * airports.length)]]
        for (let j = 0; j < freq; j++) {
            let random_month = month || 7
            let random_day = day || Math.floor(Math.random() * 31)
            let com = company[Math.floor(Math.random() * company.length)]
            let se = time[Math.floor(Math.random() * time.length)]
            let start = se[0]
            start = String(start).split(':')
            let start_hour = start[0]
            let start_min = start[1]
            let end = se[1]
            end = String(end).split(':')
            let end_hour = end[0]
            let end_min = end[1]

            let tomorrow = parseInt(start_hour) * 60 + parseInt(start_min) > parseInt(end_hour) * 60 + parseInt(end_min) ? true : false

            let start_fixed = '2022' + (random_month < 10 ? '0' : '') + random_month + (random_day < 10 ? '0' : '') + random_day + start[0] + start[1]
            let end_fixed = '2022' + (random_month < 10 ? '0' : '') + random_month + (random_day < 10 ? '0' : '') + (tomorrow ? random_day + 1 : random_day) + end[0] + end[1]

            let a = Math.ceil(price[0] * (Math.random() * 0.4 + 0.8))
            let b = Math.ceil(price[1] * (Math.random() * 0.4 + 0.8))
            let c = Math.ceil(price[2] * (Math.random() * 0.4 + 0.8))

            f2.push([com[1], Math.ceil(Math.random() * 4000) + 1000, code1, code2, start_fixed, end_fixed, a + ',' + b + ',' + c])
        }
    }

    console.log(f2.length)
    fs.writeFileSync('ticket_fixed.json', JSON.stringify(f2), err => {
        console.log('writeFileErr:' + err)
    })
    if (callback) {
        callback(f2)
    }
}

module.exports = gen
