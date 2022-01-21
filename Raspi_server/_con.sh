# /bin/bash
CPU_TEMP() {
    echo $(cat /sys/class/thermal/thermal_zone0/temp)
}
CPU_FREQ() {
    echo $(vcgencmd measure_clock arm | awk -F '=' '{print $2}')
}
CPU_USED() {
    echo $(top -n1 | awk '/Cpu\(s\):/ {print $8}')
}
MEM_ALL() {
    echo $(free | awk '/Mem:/ {print $2}')
}
MEM_USED() {
    echo $(free | awk '/Mem:/ {print $3}')
}
echo {\"CPU_TEMP\":$(CPU_TEMP)\,\"CPU_FREQ\":$(CPU_FREQ)\,\"CPU_USED\":\"$(CPU_USED)\"\,\"MEM_ALL\":$(MEM_ALL)\,\"MEM_USED\":$(MEM_USED)}