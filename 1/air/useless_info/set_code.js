const fs = require('fs')

let f0 = JSON.parse(fs.readFileSync('ticket.json').toString('utf-8'))

let airport = new Set()

f0.forEach(item => {
    airport.add(item[5])
    airport.add(item[6])
})

airport = Array.from(airport)

airport_code = {}

airport.forEach(item => {
    let c1 = String.fromCharCode('A'.charCodeAt(0) + Math.floor(Math.random() * 26))
    let c2 = String.fromCharCode('A'.charCodeAt(0) + Math.floor(Math.random() * 26))
    let c3 = String.fromCharCode('A'.charCodeAt(0) + Math.floor(Math.random() * 26))
    airport_code[item] = c1 + c2 + c3
})

fs.writeFileSync('airport_code.json', JSON.stringify(airport_code), err => {
    console.log('writeFileErr:' + err)
})
