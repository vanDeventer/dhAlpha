import logging
import asyncio
import sys
from DH_config import orchestration_config as orch_cfg

from aiocoap import *

logging.basicConfig(level=logging.INFO)

async def orchestration(service, argv):
	protocol = await Context.create_client_context()

	orch_uri='coap://[{}]:{}/{}/{}'.format(orch_cfg['ip'], orch_cfg['port'], orch_cfg[service], argv)

	print(orch_uri)

	request = Message(code=GET, uri=orch_uri)
	try:
		response = await protocol.request(request).response
	except Exception as e:
		print('Failed to fetch resource:')
		print(e)
	else:
		print('Result: %s\n%r'%(response.code, response.payload))

if __name__ == '__main__':
	asyncio.get_event_loop().run_until_complete(orchestration(sys.argv[1], sys.argv[2]))
