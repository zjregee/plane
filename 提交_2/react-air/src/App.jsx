import './App.css'
import { useEffect, useState } from 'react'
import axios from 'axios'

function App() {
    const [single, setSingle] = useState(true)
    const [which, setWhich] = useState(0)
    const [from, setFrom] = useState('首都机场-PEK')
    const [to, setTo] = useState('上海虹桥国际机场-SHA')
    const [to2, setTo2] = useState([])
    const [list, setList] = useState([])
    const [list2, setList2] = useState([])
    const [time, setTime] = useState('2022-07-08')
    const [time2, setTime2] = useState([])
    const [p1, setP1] = useState(1)
    const [p2, setP2] = useState(0)
    const [p3, setP3] = useState(0)
    useEffect(() => {
        let from = document.querySelector('.from_and_to>.from')
        let to = document.querySelector('.from_and_to>.to')
        let chooseLoc = document.querySelector('.chooseLoc')
        chooseLoc.style.opacity = 0
        from.addEventListener('click', e => {
            setWhich(0)
            chooseLoc.style.left = 0
            chooseLoc.style.opacity = 1
        })
        to.addEventListener('click', e => {
            setWhich(1)
            chooseLoc.style.left = '300px'
            chooseLoc.style.opacity = 1
        })
        chooseLoc.addEventListener('mouseleave', e => {
            setWhich(-1)
            chooseLoc.style.left = '-1000px'
            chooseLoc.style.opacity = 0
        })
    }, [])
    useEffect(() => {
        axios
            .post(
                'http://118.31.53.13:8333/recommend/second',
                {
                    times: time.split('-').join(''),
                    departures: from.split('-')[1],
                    arrival: to.split('-')[1],
                    passengerNum: parseInt(p1) + parseInt(p2)
                },
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            )
            .then(res => {
                console.log(res.data)
                setList(res.data)
            })
            .catch(err => {
                console.log(err)
            })
        let newList2 = to2.map(item => [])
        to2.forEach((item, index) => [
            axios
                .post(
                    'http://118.31.53.13:8333/recommend/second',
                    {
                        times: time2[index].split('-').join(''),
                        departures: index ? to2[index - 1].split('-')[1] : to.split('-')[1],
                        arrival: to2[index].split('-')[1],
                        passengerNum: parseInt(p1) + parseInt(p2)
                    },
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
                )
                .then(res => {
                    newList2[index] = res.data
                    setList2(newList2)
                    console.log(newList2)
                })
        ])
    }, [from, to, time, p1, p2, p3, to2, time2])
    return (
        <div className="App">
            <div className="left"></div>
            <div className="center">
                <div className="header">机票查询</div>
                <div className="main">
                    <div className="search_box">
                        <div className="single">
                            <input
                                type="radio"
                                name="single"
                                id=""
                                defaultChecked
                                onClick={e => {
                                    setTo2([])
                                    setTime2([])
                                    setSingle(true)
                                }}
                            />{' '}
                            单程
                            <input type="radio" name="single" id="" onClick={e => setSingle(false)} /> 多程
                        </div>
                        <div className="filters" style={single ? { display: 'flex' } : { display: 'block' }}>
                            <div className="from_and_to">
                                <div className={`from ${which == 0 ? 'choosen' : ''}`}>
                                    <div className="title">出发地</div>
                                    <div className="value">{from.split('-')[0]}</div>
                                </div>
                                {/* <div
                                    className="exchange"
                                    onClick={e => {
                                        let temp = from
                                        setFrom(to)
                                        setTo(temp)
                                    }}
                                >
                                    X
                                </div> */}
                                <div className={`to ${which == 1 ? 'choosen' : ''}`}>
                                    <div className="title">{!single && to2.length ? '中转地' : '目的地'}</div>
                                    <div className="value">{to.split('-')[0]}</div>
                                </div>
                                {!single &&
                                    to2.map((item, index) => {
                                        return (
                                            <div
                                                className={`to ${which == index + 2 ? 'choosen' : ''}`}
                                                style={{ borderLeft: '1px solid #e3e3e3' }}
                                                onClick={e => {
                                                    let chooseLoc = document.querySelector('.chooseLoc')
                                                    setWhich(2 + index)
                                                    chooseLoc.style.left = '300px'
                                                    chooseLoc.style.opacity = 1
                                                }}
                                            >
                                                <div className="title">{index === to2.length - 1 ? '目的地' : '中转地'}</div>
                                                <div className="value">{item.split('-')[0]}</div>
                                            </div>
                                        )
                                    })}
                                {!single && (
                                    <div
                                        className="to"
                                        style={{ borderLeft: '1px solid #e3e3e3' }}
                                        onClick={e => {
                                            let newTo2 = to2.concat(['首都机场-PEK'])
                                            let newList2 = list2.concat([[]])
                                            let newTime2 = time2.concat(['2022-07-08'])
                                            setTo2(newTo2)
                                            setList2(newList2)
                                            setTime2(newTime2)
                                        }}
                                    >
                                        <div className="title">加程</div>
                                        <div className="value">再加一程</div>
                                    </div>
                                )}
                                <div className="chooseLoc">
                                    <div className="where">
                                        <div>国内</div>
                                    </div>
                                    <div className="list">
                                        <div className="filter">
                                            <div className="choosen1">热门</div>
                                        </div>
                                        <div className="itemList">
                                            {Object.keys(code).map(item => {
                                                return (
                                                    <div
                                                        key={item}
                                                        className="item"
                                                        onClick={e => {
                                                            if (which === 0) {
                                                                setFrom(item + '-' + code[item])
                                                            } else if (which === 1) {
                                                                setTo(item + '-' + code[item])
                                                            } else if (which !== -1) {
                                                                let newTo2 = to2.concat([])
                                                                newTo2[which - 2] = item + '-' + code[item]
                                                                setTo2(newTo2)
                                                            }
                                                            setWhich(-1)
                                                            document.querySelector('.chooseLoc').style.opacity = 0
                                                        }}
                                                    >
                                                        {item}
                                                    </div>
                                                )
                                            })}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="start_time">
                                <div className="title">出发时间</div>
                                <div className="value">
                                    <input className="date1" type="date" name="" id="" value={time} onChange={e => setTime(e.target.value)} />
                                    {time2.map((item, index) => {
                                        return (
                                            <input
                                                className="date1"
                                                type="date"
                                                name=""
                                                id=""
                                                value={item}
                                                onChange={e => {
                                                    let value = e.target.value
                                                    let newTime2 = time2.concat([])
                                                    newTime2[index] = value
                                                    setTime2(newTime2)
                                                }}
                                            />
                                        )
                                    })}
                                </div>
                            </div>
                            <div className="type">
                                <div className="title">乘客类型</div>
                                <div className="value">
                                    <div className="adult">
                                        <input type="number" name="" id="" value={p1} onChange={e => setP1(e.target.value >= 0 ? e.target.value : 0)} />
                                        成人
                                    </div>
                                    <div className="child">
                                        <input type="number" name="" id="" value={p2} onChange={e => setP2(e.target.value >= 0 ? e.target.value : 0)} />
                                        儿童
                                    </div>
                                    <div className="baby">
                                        <input type="number" name="" id="" value={p3} onChange={e => setP3(e.target.value >= 0 ? e.target.value : 0)} />
                                        婴儿
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="search_result">
                        <div className="result_list">
                            <div className="title">
                                <div className="p1">{single ? '单程' : '第1程'}:</div>
                                <div className="p2">{from.split('-')[0]}</div>
                                <div className="p3"></div>
                                <div className="p4">{to.split('-')[0]}</div>
                                <div className="p5">
                                    {time.split('-')[1]}月{time.split('-')[2]}日 星期{new Date(`2022/${time.split('-')[1]}/${time.split('-')[2]}`).getDay() || '日'}
                                </div>
                            </div>
                            <div className="list">
                                {list.map((item, index) => {
                                    return (
                                        <div className="ticket" key={index}>
                                            <div className="company">
                                                <div className="name">{item.path[0].carrier[0]}</div>
                                                <div className="info">
                                                    {item.path[0].carrier.length}个航司 {item.path.length}程航班
                                                </div>
                                            </div>
                                            <div className="time">
                                                <div className="start">
                                                    <div className="time">{item.path[0].startTime.slice(8, 10) + ':' + item.path[0].startTime.slice(10, 12)}</div>
                                                    <div className="loc">{from.split('-')[0]}T1</div>
                                                </div>
                                                <div className="turn">
                                                    <div className="count">转{item.path.length - 1}次</div>
                                                    <div className="loc">--</div>
                                                </div>
                                                <div className="end">
                                                    <div className="time">{item.path[item.path.length - 1].endTime.slice(8, 10) + ':' + item.path[item.path.length - 1].endTime.slice(10, 12)}</div>
                                                    <div className="loc">{to.split('-')[0]}T1</div>
                                                </div>
                                            </div>
                                            <div className="cost">
                                                {Math.floor(parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) > 0
                                                    ? Math.floor(
                                                          (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) / 60
                                                      )
                                                    : Math.floor(
                                                          (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) / 60
                                                      ) +
                                                      23 +
                                                      (Math.floor(
                                                          parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))
                                                      ) > 0
                                                          ? -1
                                                          : 1)}
                                                小时
                                                {(Math.floor(parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) >
                                                0
                                                    ? Math.floor(
                                                          (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) % 60
                                                      )
                                                    : Math.floor(
                                                          (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))) % 60
                                                      ) + 60) % 60}
                                                分
                                            </div>
                                            <div className="detail">航班详情</div>
                                            <div className="price">
                                                <div className="info">
                                                    <div className="number">
                                                        <div className="part1">￥</div>
                                                        <div className="part2">{item.price}</div>
                                                        <div className="part3">起</div>
                                                    </div>
                                                    <div className="tax">含税价</div>
                                                </div>
                                                <div className="order" onClick={order}>
                                                    订票
                                                </div>
                                            </div>
                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                        {to2.map((item, index) => {
                            return (
                                <div className="result_list">
                                    <div className="title">
                                        <div className="p1">{`第${index + 2}程`}:</div>
                                        <div className="p2">{index ? to2[index - 1].split('-')[0] : to.split('-')[0]}</div>
                                        <div className="p3"></div>
                                        <div className="p4">{to2[index].split('-')[0]}</div>
                                        <div className="p5">
                                            {time2[index].split('-')[1]}月{time2[index].split('-')[2]}日 星期{new Date(`2022/${time2[index].split('-')[1]}/${time2[index].split('-')[2]}`).getDay() || '日'}
                                        </div>
                                    </div>
                                    <div className="list">
                                        {list2 &&
                                            list2[index] &&
                                            list2[index].map((item, index2) => {
                                                return (
                                                    <div className="ticket" key={index2}>
                                                        <div className="company">
                                                            <div className="name">{item.path[0].carrier[0]}</div>
                                                            <div className="info">
                                                                {item.path[0].carrier.length}个航司 {item.path.length}程航班
                                                            </div>
                                                        </div>
                                                        <div className="time">
                                                            <div className="start">
                                                                <div className="time">{item.path[0].startTime.slice(8, 10) + ':' + item.path[0].startTime.slice(10, 12)}</div>
                                                                <div className="loc">{index ? to2[index - 1].split('-')[0] : to.split('-')[0]}T1</div>
                                                            </div>
                                                            <div className="turn">
                                                                <div className="count">转{item.path.length - 1}次</div>
                                                                <div className="loc">--</div>
                                                            </div>
                                                            <div className="end">
                                                                <div className="time">{item.path[item.path.length - 1].endTime.slice(8, 10) + ':' + item.path[item.path.length - 1].endTime.slice(10, 12)}</div>
                                                                <div className="loc">{to2[index].split('-')[0]}T1</div>
                                                            </div>
                                                        </div>
                                                        <div className="cost">
                                                            {Math.floor(
                                                                parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))
                                                            ) > 0
                                                                ? Math.floor(
                                                                      (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 +
                                                                          parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) -
                                                                          parseInt(item.path[0].startTime.slice(8, 10)) * 60 -
                                                                          parseInt(item.path[0].startTime.slice(10, 12))) /
                                                                          60
                                                                  )
                                                                : Math.floor(
                                                                      (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 +
                                                                          parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) -
                                                                          parseInt(item.path[0].startTime.slice(8, 10)) * 60 -
                                                                          parseInt(item.path[0].startTime.slice(10, 12))) /
                                                                          60
                                                                  ) +
                                                                  23 +
                                                                  (Math.floor(
                                                                      parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 +
                                                                          parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) -
                                                                          parseInt(item.path[0].startTime.slice(8, 10)) * 60 -
                                                                          parseInt(item.path[0].startTime.slice(10, 12))
                                                                  ) > 0
                                                                      ? -1
                                                                      : 1)}
                                                            小时
                                                            {(Math.floor(
                                                                parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 + parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) - parseInt(item.path[0].startTime.slice(8, 10)) * 60 - parseInt(item.path[0].startTime.slice(10, 12))
                                                            ) > 0
                                                                ? Math.floor(
                                                                      (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 +
                                                                          parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) -
                                                                          parseInt(item.path[0].startTime.slice(8, 10)) * 60 -
                                                                          parseInt(item.path[0].startTime.slice(10, 12))) %
                                                                          60
                                                                  )
                                                                : Math.floor(
                                                                      (parseInt(item.path[item.path.length - 1].endTime.slice(8, 10)) * 60 +
                                                                          parseInt(item.path[item.path.length - 1].endTime.slice(10, 12)) -
                                                                          parseInt(item.path[0].startTime.slice(8, 10)) * 60 -
                                                                          parseInt(item.path[0].startTime.slice(10, 12))) %
                                                                          60
                                                                  ) + 60) % 60}
                                                            分
                                                        </div>
                                                        <div className="detail">航班详情</div>
                                                        <div className="price">
                                                            <div className="info">
                                                                <div className="number">
                                                                    <div className="part1">￥</div>
                                                                    <div className="part2">{item.price}</div>
                                                                    <div className="part3">起</div>
                                                                </div>
                                                                <div className="tax">含税价</div>
                                                            </div>
                                                            <div className="order" onClick={order}>
                                                                订票
                                                            </div>
                                                        </div>
                                                    </div>
                                                )
                                            })}
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                </div>
            </div>
        </div>
    )
    function order() {
        alert('订票成功')
    }
}

export default App

const code = {
    阿尔山伊尔施机场: 'USE',
    北京南苑机场: 'NAY',
    杭州萧山国际机场: 'FIP',
    呼和浩特白塔国际机场: 'XEJ',
    伊尔施机场: 'UIJ',
    滨海机场: 'MXV',
    咸阳机场: 'KZO',
    阿克苏机场: 'TWD',
    首都机场: 'LRX',
    成都双流国际机场: 'DCI',
    萧山机场: 'SXI',
    流亭机场: 'GJC',
    上海虹桥国际机场: 'SHA',
    地窝堡机场: 'VYN',
    重庆江北国际机场: 'LRH',
    阿勒泰机场: 'BOD',
    安庆天柱山机场: 'EGN',
    广州新白云国际机场: 'VFD',
    天柱山机场: 'IMW',
    虹桥机场: 'ZKV'
}
