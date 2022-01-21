// 导入http模块:
const express = require('express');
const app = express()
const child_process = require('child_process');

app.get("/", function (req, res) {
    res.send("RaspberryPi Control")
})

app.get("/raspi_base_info", function (request, response) {
    const result_base_info = child_process.execSync('bash /root/nodejs/raspberry_con/con.sh')
    response.end(result_base_info)
})

app.get("/clash_start", function (request, response) {
    try {
        child_process.execSync('/etc/clash/start.sh start')
    } catch (e) {
        response.end("502")
    }
    response.end("200")
})

app.get("/clash_stop", function (request, response) {
    try {
        child_process.execSync('/etc/clash/start.sh stop')
    } catch (e) {
        response.end("502")
    }
    response.end("200")
})

app.get("/clash_updateyaml", function (request, response) {
    try {
        if (request.query.url !== undefined) {
            const value = "sed -i \"s#Url=.*#Url=\\'" + request.query.url + "\\'#g\" /etc/clash/mark"
            child_process.execSync(value)
            child_process.execSync('/etc/clash/start.sh updateyaml')
        }
        else
            child_process.execSync('/etc/clash/start.sh updateyaml')
    } catch (e) {
        response.end("502")
    }
    response.end("200")
})

app.listen(8080)
console.log('Server is running at http://127.0.0.1:8080/');