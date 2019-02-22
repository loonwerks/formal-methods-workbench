update-footer := cat footer-head > footer; date >> footer; cat footer-tail >> footer
mypandoc      := pandoc


all: html pdf
# all: html

html: splat.html

pdf: splat.pdf

splat.html: splat.txt
	$(update-footer)
	$(mypandoc) -s -S\
               -c mypandoc.css\
               -A footer\
               --toc\
               --toc-depth=6\
               --mathjax\
               splat.txt\
               -o splat.html 

splat.pdf: splat.txt
	$(mypandoc) -s -S\
               -B toc.tex\
               --latex-engine=xelatex\
               splat.txt\
               -o splat.pdf

# Delete HTML and PDF files

clean:
	rm -f splat.html
	rm -f splat.pdf
