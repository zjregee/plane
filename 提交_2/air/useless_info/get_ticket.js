const http = require('http')
const fs = require('fs')

let url = 'http://www.esk365.com/tools/gnhb/hbshow.asp?id='

let ids = JSON.parse(fs.readFileSync('flight_id.json').toString('utf-8'))

let tickets = []

let old_ticket = JSON.parse(fs.readFileSync('ticket.json').toString('utf-8'))
console.log('count:' + old_ticket.length)

function getTicket(index) {
    console.log(index)
    let req = http.request(url + ids[index], res => {
        let chunks = []
        res.on('data', chunk => {
            chunks.push(chunk)
        })
        res.on('end', () => {
            let html = Buffer.concat(chunks).toString('utf-8')
            let reg = /<table[\s\S]*<\/table>/
            let table = html.match(reg)[0]
            let trs = table.split('</tr>')
            let reg2 = /[\u4e00-\u9fa5]{2,10}/g
            let reg3 = />([\d\w]{2,6})</g
            let reg4 = /[\d]{1,2}:[\d]{2}/g

            let company = trs[1].match(reg2)
            company = company.slice(0, 1)
            let number_and_type = trs[1].match(reg3)
            number_and_type = number_and_type.map(item => item.slice(1, item.length - 1))
            number_and_type = number_and_type.slice(0, 2)
            let start_and_end = trs[1].match(reg4)
            let from_and_to = trs[2].match(reg2)

            let ticket = company.concat(number_and_type).concat(start_and_end).concat(from_and_to)
            tickets.push(ticket)
            if (index % 5 == 0) {
                let old_ticket = JSON.parse(fs.readFileSync('ticket.json').toString('utf-8'))
                tickets = old_ticket.concat(tickets)
                fs.writeFileSync('ticket.json', JSON.stringify(tickets), err => {
                    console.log('writeFileErr:' + err)
                })
                tickets = []
            }
            if (index < ids.length - 1) {
                setTimeout(() => {
                    getTicket(index + 1)
                }, 200)
            } else {
                fs.writeFileSync('ticket.json', JSON.stringify(tickets), err => {
                    console.log('writeFileErr:' + err)
                })
            }
        })
    })

    req.end()
}

getTicket(old_ticket.length)
