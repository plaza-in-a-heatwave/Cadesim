#!/usr/bin/env python3

# quick script to shutdown all running servers.
# to run: python3 stop_servers.py

import time
import os

INSTANCE_FILENAME = ".CADESIM_INSTANCE_"
STOP_FILENAME     = ".STOP"
MAX_WAIT_TIME=20

def count_active_instances():
    return len([x for x in os.listdir() if x.startswith(INSTANCE_FILENAME)])

try:
    # drop the stop file
    with open(STOP_FILENAME, "w") as f:
        pass

    # wait up to n seconds
    active_instances = 0
    all_instances_closed = False
    for i in range(MAX_WAIT_TIME):
        active_instances = count_active_instances()
        if active_instances == 0:
            all_instances_closed = True
            break
        print("shutting down servers... " + str(MAX_WAIT_TIME - i) + "s remaining, waiting on " + str(active_instances) + " servers")
        time.sleep(1.0)

    if all_instances_closed:
        print("success: all Cadesim instances stopped.")
    else:
        print(
            "warning: couldn't stop {} Cadesim instances. (maybe some servers crashed?)".format(
                active_instances
            )
        )
        print("if the servers have crashed, please delete the following files: {}".format(
                [x for x in os.listdir() if x.startswith(INSTANCE_FILENAME)]
            )
        )
except KeyboardInterrupt:
    pass
finally:
    # remove the stop file
    try:
        os.remove(STOP_FILENAME)
    except FileNotFoundError:
        pass