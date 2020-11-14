#!/usr/bin/env python3

# example script that launches some Cadesim servers. Feel free to change these arguments.
# to run: python3 start_servers.py

import subprocess, platform, time

# windows fix for Popen not detaching https://stackoverflow.com/a/13593257
# (close_fds arg is the equivalent for linux)
DETACHED_PROCESS = 0x00000008

for server_args in [
    "java -jar cadesim-server.jar -p 4970 --schedule-updates 04:00",
    "java -jar cadesim-server.jar -p 4971 --schedule-updates 04:00",
    "java -jar cadesim-server.jar -p 4972 --schedule-updates 04:00",
    "java -jar cadesim-server.jar -p 4973 --schedule-updates 04:00",
    "java -jar cadesim-server.jar -p 4974 --schedule-updates 04:00",
]:
    time.sleep(0.1) # add time granularity to prevent two servers sharing same logfile
    if(any(platform.win32_ver())):
        print("started Cadesim instance (pid: " + str(subprocess.Popen(server_args.split(" "), close_fds=True, creationflags=DETACHED_PROCESS).pid) + ")")
    else:
        print("started Cadesim instance (pid: " + str(subprocess.Popen(server_args.split(" "), close_fds=True).pid) + ")")
print("\n\nDone! You can now close this window.")
print("Use stop_servers.py to stop, or restart_servers.py to restart.")
