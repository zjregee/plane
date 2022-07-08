const fs = require('fs')

let f0 = JSON.parse(fs.readFileSync('flight_name.json').toString('utf-8'))

let name = new Set()

f0.forEach(item => {
    let loc = item.split('-')
    name.add(loc[0])
    name.add(loc[1])
})

name = Array.from(name)

fs.writeFileSync('name.json', JSON.stringify(name), err => {
    console.log('writeFileErr:' + err)
})
