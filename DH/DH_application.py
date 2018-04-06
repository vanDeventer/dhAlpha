#!/usr/bin/env python3

# This file is part of the Python aiocoap library project.
#
# Copyright (c) 2012-2014 Maciej Wasilak <http://sixpinetrees.blogspot.com/>,
#               2013-2014 Christian Amsüss <c.amsuess@energyharvesting.at>
#
# aiocoap is free software, this file is published under the MIT license as
# described in the accompanying LICENSE file.

"""This is a usage example of aiocoap that demonstrates how to implement a
simple client. See the "Usage Examples" section in the aiocoap documentation
for some more information."""

import logging
import asyncio
import json
from pprint import pprint
from sd_client import servicediscovery

from aiocoap import *

logging.basicConfig(level=logging.INFO)

from DH_config import servicediscovery_config as sd_cfg,\
					  orchestrator_config as os_cfg,\
					  orchestration_configuration as orch_conf_json

async def get_ahf_uri(orchestration_configuration):
	protocol = await Context.create_client_context()

	""" Some function that gets the orchestration rules from the orchestrator """
	orch_conf = json.loads(orchestration_configuration)

	if orch_conf['target'] != 'DH-application':
		print("Loaded rules do not contain the correct target")
		return

	""" Load rules into a single dict """
	orch_rules = dict(pair for rule in orch_conf['rules'] for pair in rule.items())
	
	ahf_services = {}
	for location, service_protocol in orch_rules.items():
		service = service_protocol.split(',')[0]
		prtcl = service_protocol.split(',')[1]
		if prtcl != '_coap':
			print("protocol is not coap")
			return
		service_instance = (await servicediscovery('lookup', service))[0]
		ahf_services[location] = 'coap://[{}]:{}{}'.format(service_instance['host'], service_instance['port'], service_instance['properties']['property'][0]['value'])
	
	return ahf_services

async def control_loop(requested_temperature, ahf_services):
	protocol = await Context.create_client_context()

	locations = ['Outside temperature', 'Pipe temperature']

	temperature = {}
	for location in locations:
		temperature_uri = ahf_services[location];

		request = Message(code=GET, uri=temperature_uri)
		try:
			response = await protocol.request(request).response
		except Exception as e:
			print('Failed to fetch resource:')
			print(e)
		else:
			temp_data = json.loads(response.payload.decode('utf-8'))['e'][0]['v']
			temperature[location.split(' ')[0]] = temp_data/1000
	
	actuation = int(100*(requested_temperature - temperature['Outside'])/temperature['Pipe'])
	
	actuation_payload = '{{\"e\":[{{\"n\":\"valveactuation\",\"u\":\"%\",\"v\":{}}}]}}'.format(actuation) 

	#print(actuation_payload)
	valve_uri = ahf_services['Valve Actuator']

	#print(valve_uri)

	request = Message(code=POST, uri=valve_uri, payload=actuation_payload.encode())
	try:
		response = await protocol.request(request).response
	except Exception as e:
		print('Failed to post resource:')
		print(e)
	else:
		pass

	print('{}\nTemperatures: \n\tOutside:\t{}\n\tPipe:\t\t{}\n\tRequested:\t{}\n\nActuation: {} %\n'.format('-'*20,temperature['Outside'], temperature['Pipe'], requested_temperature, actuation))
	await asyncio.sleep(2)


if __name__ == "__main__":
	loop = asyncio.get_event_loop()
	ahf_services = loop.run_until_complete(get_ahf_uri(orch_conf_json))

	while True:
		loop.run_until_complete(control_loop(25, ahf_services))





