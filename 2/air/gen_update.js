const gen = require('./gen.js')
const update = require('./update.js')

args = process.argv.slice(2, 6)
while (args.length < 4) {
    args.push(1)
}
args = args.map(item => parseInt(item))

console.log(args)
gen(...args, update)

// gen(1000, 2, 7, 8, update)
