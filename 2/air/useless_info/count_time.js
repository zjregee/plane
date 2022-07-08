const fs = require('fs')

let f0 = JSON.parse(fs.readFileSync('ticket.json').toString('utf-8'))

let time = new Set()

f0.forEach(item => {
    time.add([item[3], item[4]])
})

time = Array.from(time)

fs.writeFileSync('time.json', JSON.stringify(time), err => {
    console.log('writeFileErr:' + err)
})
