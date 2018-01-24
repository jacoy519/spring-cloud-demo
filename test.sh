#!/bin/bash
start_date=$1
end_date=$2
next_date=${start_date}
while [[ $next_date < $end_date ]]
do
	next_date=$(date -d "${next_date} next-day" "+%Y-%m-%d")
	echo "simulation date $next_date"
        session_id=$(curl -k -X POST --data "actiorname=azkaban&password=azkaban" "http://localhost:10401"|sed 's/.*session\.id":"\(.*\)".*/\1/g')
	echo $session_id
	exec_id=$(curl -k --get --data "session.id=${session_id}" --data "ajax=executeFlow" --data "project=azkaban-test-project" --data "flow=test" "http://localhost:10401/executor" |sed 's/.*execid:\([0-9]*\).*/\1/g')
	echo $exec_id
	while true
	do
		status=$(curl -k --get --data "session.id=${session_id}&ajax=getRunning&project=azkaban-test-project&flow=test" "http://localhost:10401/executor" |sed 's/.*execIds":\(.*\)}/\1/g')
		echo $status
		result=$(echo $status | grep "$exec_id")
		if [[ "$result" != "" ]]
		then
			echo "simualtion job $exec_id has been finished"
			break
		else
			echo "simualtion job $exec_id is running"
			sleep 30
		fi
	done
done

