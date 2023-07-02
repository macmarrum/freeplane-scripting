#!/usr/bin/python3
import sys
import base64
from pathlib import Path

from groovy_scripting_client import transfer

me = Path(__file__)
encoding = 'UTF-8'

# path to the json file is the first argument to the script
json_path = Path(sys.argv[1])
json_data = json_path.read_text(encoding=encoding)
json_base64 = base64.b64encode(json_data.encode(encoding)).decode('L1')
groovy_script = f"""
import io.github.macmarrum.freeplane.Import
Import.importJsonBase64('{json_base64}', node.mindMap.root)
"""
response = transfer(groovy_script)
print(response)
