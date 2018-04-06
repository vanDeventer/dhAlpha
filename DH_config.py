import json

orchestrator_config = {
			'ip':'fdfd::ff',
			'port':'9684',
			'store':'orchestrationstore/configuration',
			'engine':'orchestrationengine'
			}

servicediscovery_config = {
			'ip':'fdfd::ff',
			'port':'9683',
			'service':'servicediscovery/service',
			'type':'servicediscovery/type'
			}

orchestration_configuration = json.dumps({
    "target":"DH-application",
    "rules":[
         {"Outside temperature":"_Temp-7bd03c64679f9089,_coap"},
         {"Pipe temperature":"_Temp-975175ae7952431e,_coap"},
         {"Valve Actuator":"_ValveActuator-f67523cf9966e868,_coap"},
        ]
	})
