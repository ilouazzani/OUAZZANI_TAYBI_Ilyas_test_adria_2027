"""
Convertit RAPPORT_EPREUVE_TECHNIQUE.md en un fichier HTML élégant prêt pour l'impression en PDF (Ctrl+P -> Enregistrer en PDF)
"""
import re
import os

md_path = "RAPPORT_EPREUVE_TECHNIQUE.md"
html_path = "RAPPORT_EPREUVE_TECHNIQUE.html"

with open(md_path, "r", encoding="utf-8") as f:
    content = f.read()

# Remplacement simple des balises markdown courantes pour un rendu propre
# Titres
content = re.sub(r'^# (.*?)$', r'<h1>\1</h1>', content, flags=re.MULTILINE)
content = re.sub(r'^## (.*?)$', r'<h2>\1</h2>', content, flags=re.MULTILINE)
content = re.sub(r'^### (.*?)$', r'<h3>\1</h3>', content, flags=re.MULTILINE)
content = re.sub(r'^#### (.*?)$', r'<h4>\1</h4>', content, flags=re.MULTILINE)

# Images
content = re.sub(r'!\[(.*?)\]\((.*?)\)', r'<figure><img src="\2" alt="\1" class="shadow-md rounded-lg my-4 max-w-full"><figcaption class="text-xs text-gray-500 text-center mt-1">\1</figcaption></figure>', content)

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
    <title>Rapport Épreuve Technique - ENSET / ADRIA 2027</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script type="module">
        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
        mermaid.initialize({{ startOnLoad: true }});
    </script>
    <style>
        @media print {{
            @page {{ margin: 15mm; size: A4; }}
            body {{ font-size: 11pt; }}
            .no-print {{ display: none; }}
            h1, h2, h3 {{ page-break-after: avoid; }}
            pre, blockquote, figure {{ page-break-inside: avoid; }}
        }}
    </style>
</head>
<body class="bg-slate-100 py-8 text-slate-800">
    <div class="no-print max-w-4xl mx-auto mb-4 flex justify-between items-center bg-blue-700 text-white p-4 rounded-xl shadow">
        <span>📄 Rapport technique prêt pour export PDF</span>
        <button onclick="window.print()" class="bg-white text-blue-800 px-4 py-2 rounded-lg font-bold text-sm shadow hover:bg-blue-50">
            🖨️ Imprimer / Enregistrer en PDF
        </button>
    </div>
    <div class="max-w-4xl mx-auto bg-white p-10 rounded-xl shadow-lg border border-slate-200">
        {body}
    </div>
</body>
</html>
"""

with open(html_path, "w", encoding="utf-8") as f:
    f.write(html_template)

print(f"Generated {html_path} successfully!")
