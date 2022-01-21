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

app.listen(8080)
console.log('Server is running at http://127.0.0.1:8080/');