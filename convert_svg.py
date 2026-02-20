import re

with open(r'C:\Users\bhrag\AndroidStudioProjects\ImageToPDF\app\src\main\res\drawable\illustration.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Extract all path elements with their fill and opacity
path_pattern = re.compile(r'<path fill="(#[0-9A-Fa-f]+)" opacity="([0-9.]+)" stroke="none" d="(.*?)"', re.DOTALL)
matches = path_pattern.findall(content)
print(f'Found {len(matches)} paths')

def convert_path_data(d):
    """Convert Photoshop SVG path format to standard SVG path format"""
    d = d.strip()
    lines = d.split('\n')
    result = []
    for line in lines:
        line = line.strip()
        if not line:
            continue
        # Replace 'M x,y' -> 'M x y'
        # Replace 'C x1,y1 x2,y2 x,y' -> 'C x1 y1 x2 y2 x y'
        line = line.replace(',', ' ')
        result.append(line)
    return ' '.join(result)

# Build the vector drawable XML
out_lines = []
out_lines.append('<?xml version="1.0" encoding="utf-8"?>')
out_lines.append('<vector xmlns:android="http://schemas.android.com/apk/res/android"')
out_lines.append('    android:width="512dp"')
out_lines.append('    android:height="453dp"')
out_lines.append('    android:viewportWidth="614"')
out_lines.append('    android:viewportHeight="453">')

for fill, opacity, d in matches:
    path_data = convert_path_data(d)
    alpha = int(float(opacity) * 255)
    # Convert fill color with alpha
    fill_hex = fill.lstrip('#')
    if len(fill_hex) == 6:
        fill_with_alpha = f'#{alpha:02X}{fill_hex}'
    else:
        fill_with_alpha = f'#{fill_hex}'

    out_lines.append(f'    <path')
    out_lines.append(f'        android:fillColor="{fill_with_alpha}"')
    out_lines.append(f'        android:pathData="{path_data}" />')

out_lines.append('</vector>')

output = '\n'.join(out_lines)

with open(r'C:\Users\bhrag\AndroidStudioProjects\ImageToPDF\app\src\main\res\drawable\illustration.xml', 'w', encoding='utf-8') as f:
    f.write(output)

print(f'Written {len(matches)} paths to illustration.xml')
print('First path data sample:')
if matches:
    print(convert_path_data(matches[0][2])[:200])

