import requests

key = 'cc42279a7db889b6'
ApiUrl = 'http://api.wunderground.com/api/' +key + '/conditions/q/SE/Lulea.json'

r = requests.get(ApiUrl)
conditions = r.json()
print conditions['current_observation']['temp_c']
