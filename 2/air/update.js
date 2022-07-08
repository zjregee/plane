const fs = require('fs')
const axios = require('axios')

// let f2 = JSON.parse(fs.readFileSync('ticket_fixed.json').toString('utf-8'))

function update(f2, index) {
    index = index || 0
    if (index > f2.length - 1) {
        return
    }
    let item = f2[index]
    setTimeout(() => {
        console.log(index + 1 + '/' + f2.length)
        update(f2, index + 1)
    }, 500)
    axios
        .post(
            'http://118.31.53.13:8333/recommend/insert',
            {
                carrier: item[0],
                flightNum: item[1],
                departure: item[2],
                arrival: item[3],
                startTime: item[4],
                endTime: item[5],
                price: item[6]
            },
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        )
        .catch(err => {
            console.log('Update Error: ' + index)
        })
}

module.exports = update
