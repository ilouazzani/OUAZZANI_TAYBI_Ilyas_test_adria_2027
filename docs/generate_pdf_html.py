"""
Convertit RAPPORT_EPREUVE_TECHNIQUE.md en un fichier HTML élégant prêt pour l'impression en PDF (Ctrl+P -> Enregistrer en PDF)
"""
import re
import os
import base64

md_path = "RAPPORT_EPREUVE_TECHNIQUE.md"
html_path = "RAPPORT_EPREUVE_TECHNIQUE.html"

with open(md_path, "r", encoding="utf-8") as f:
    content = f.read()

# Titres
content = re.sub(r'^# (.*?)$', r'<h1>\1</h1>', content, flags=re.MULTILINE)
content = re.sub(r'^## (.*?)$', r'<h2>\1</h2>', content, flags=re.MULTILINE)
content = re.sub(r'^### (.*?)$', r'<h3>\1</h3>', content, flags=re.MULTILINE)
content = re.sub(r'^#### (.*?)$', r'<h4>\1</h4>', content, flags=re.MULTILINE)

# Remplacement des images avec encodage Base64 pour être 100% autonome et imprimable
def replace_img(match):
    alt = match.group(1)
    src = match.group(2)
    clean_src = src.replace('/', os.sep).replace('\\', os.sep)
    if os.path.exists(clean_src):
        ext = os.path.splitext(clean_src)[1].lower().replace('.', '')
        if ext == 'jpg':
            ext = 'jpeg'
        with open(clean_src, 'rb') as img_f:
            b64 = base64.b64encode(img_f.read()).decode('utf-8')
            src = f"data:image/{ext};base64,{b64}"
    return f'<figure class="my-6 text-center page-break-inside-avoid"><img src="{src}" alt="{alt}" class="shadow-lg rounded-xl mx-auto border border-slate-200 max-h-[450px] object-contain max-w-full"><figcaption class="text-xs text-slate-500 font-medium text-center mt-2 italic">Figure : {alt}</figcaption></figure>'

content = re.sub(r'!\[(.*?)\]\((.*?)\)', replace_img, content)

# Liens
content = re.sub(r'\[(.*?)\]\((.*?)\)', r'<a href="\2" class="text-blue-600 underline">\1</a>', content)

# Bold & Italic
content = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', content)
content = re.sub(r'\*(.*?)\*', r'<em>\1</em>', content)

# Blocs de code
content = re.sub(r'```mermaid([\s\S]*?)```', r'<pre class="mermaid bg-slate-100 p-4 rounded text-xs overflow-x-auto">\1</pre>', content)
content = re.sub(r'```([a-zA-Z0-9_-]*)([\s\S]*?)```', r'<pre class="bg-slate-900 text-slate-100 p-4 rounded text-xs font-mono overflow-x-auto my-3"><code>\2</code></pre>', content)
content = re.sub(r'`([^`]+)`', r'<code class="bg-slate-100 text-pink-600 px-1 py-0.5 rounded text-xs font-mono">\1</code>', content)

# Blockquotes
content = re.sub(r'^> (.*?)$', r'<blockquote class="border-l-4 border-blue-500 pl-4 py-1 italic text-slate-700 my-2 bg-blue-50">\1</blockquote>', content, flags=re.MULTILINE)

# Paragraphes
paragraphs = content.split('\n\n')
formatted_paragraphs = []
for p in paragraphs:
    p = p.strip()
    if p and not p.startswith('<h') and not p.startswith('<pre') and not p.startswith('<figure') and not p.startswith('<blockquote') and not p.startswith('<table'):
        formatted_paragraphs.append(f'<p class="mb-3 leading-relaxed text-slate-700">{p}</p>')
    else:
        formatted_paragraphs.append(p)

body = '\n'.join(formatted_paragraphs)

html_template = f"""<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Rapport Épreuve Technique - ENSET / ADRIA 2027 - Ilyas OUAZZANI TAYBI</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script type="module">
        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
        mermaid.initialize({{ startOnLoad: true }});
    </script>
    <style>
        @media print {{
            body {{ font-size: 11pt; }}
            .no-print {{ display: none !important; }}
            .page-break {{ page-break-before: always; }}
            .page-break-inside-avoid {{ page-break-inside: avoid; }}
            pre, code {{ font-size: 9pt; }}
            h1, h2, h3 {{ page-break-after: avoid; }}
            img {{ max-height: 380px !important; }}
        }}
        h1 {{ font-size: 1.875rem; font-weight: 800; color: #1e293b; border-bottom: 2px solid #3b82f6; padding-bottom: 0.5rem; margin-top: 1.5rem; margin-bottom: 1rem; }}
        h2 {{ font-size: 1.5rem; font-weight: 700; color: #1e3a8a; border-bottom: 1px solid #cbd5e1; padding-bottom: 0.25rem; margin-top: 1.5rem; margin-bottom: 0.75rem; }}
        h3 {{ font-size: 1.25rem; font-weight: 600; color: #1e40af; margin-top: 1.25rem; margin-bottom: 0.5rem; }}
        h4 {{ font-size: 1rem; font-weight: 600; color: #334155; margin-top: 1rem; margin-bottom: 0.25rem; }}
        table {{ width: 100%; border-collapse: collapse; margin: 1rem 0; font-size: 0.875rem; }}
        th, td {{ border: 1px solid #cbd5e1; padding: 0.5rem 0.75rem; text-align: left; }}
        th {{ background-color: #f1f5f9; font-weight: 600; }}
        tr:nth-child(even) {{ background-color: #f8fafc; }}
    </style>
</head>
<body class="bg-gray-50 text-slate-800 p-4 md:p-8">
    <div class="max-w-4xl mx-auto bg-white p-6 md:p-12 rounded-xl shadow-lg border border-slate-200">
        
        <div class="no-print bg-blue-50 border-l-4 border-blue-600 p-4 mb-6 rounded flex items-center justify-between">
            <div>
                <p class="font-bold text-blue-900">📄 Rapport Épreuve Technique - Format Imprimable / PDF</p>
                <p class="text-sm text-blue-700">Toutes les captures d'écran et schémas sont intégrés. Cliquez sur le bouton pour générer votre PDF.</p>
            </div>
            <button onclick="window.print()" class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded shadow transition duration-200 cursor-pointer flex items-center gap-2">
                🖨️ Imprimer / Exporter en PDF
            </button>
        </div>

        {body}
        
    </div>
</body>
</html>
"""

with open(html_path, "w", encoding="utf-8") as f:
    f.write(html_template)

print("Generated RAPPORT_EPREUVE_TECHNIQUE.html successfully with embedded base64 images!")
