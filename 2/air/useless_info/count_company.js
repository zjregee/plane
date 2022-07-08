const fs = require('fs')

let f0 = JSON.parse(fs.readFileSync('ticket.json').toString('utf-8'))

let company = new Set()

f0.forEach(item => {
    company.add([item[0], item[1].slice(0, 2)])
})

company = Array.from(company)

fs.writeFileSync('company.json', JSON.stringify(company), err => {
    console.log('writeFileErr:' + err)
})
