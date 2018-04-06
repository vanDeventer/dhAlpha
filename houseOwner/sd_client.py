import logging
import asyncio
import sys
import json
from pprint import pprint
import numpy as np
from DH_config import servicediscovery_config as sd_cfg

from aiocoap import *

logging.basicConfig(level=logging.INFO)

async def servicediscovery(service, argv = ''):
	protocol = await Context.create_client_context()

	query_results = []
	for query_type in ['service']:
		sd_uri='coap://[{}]:{}/{}/{}'.format(sd_cfg['ip'], sd_cfg['port'], sd_cfg[query_type], argv)

		request = Message(code=GET, uri=sd_uri)
		try:
			response = await protocol.request(request).response
		except Exception as e:
			print('Failed to fetch resource:')
			print(e)
		else:
			#print('Result: %s\n%r'%(response.code, response.payload))
			pass
	
		service_record = json.loads(response.payload.decode('utf-8'))
		#json.loads(response.payload.decode('utf-8')
		#pprint.pprint(service_record)

		query_results = query_results + service_record

	return query_results


if __name__ == '__main__':
	test = asyncio.get_event_loop().run_until_complete(servicediscovery(sys.argv[1], sys.argv[2]))

