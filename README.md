# Dropblog

A simple blog written in clojure and [noir](http://webnoir.org). Just drop a markdown file into your dropbox folder, and watch your post go live.

## Getting Started

Currently, this simply acts as a template to build your blog around. Soon it will be a lein plugin or something like that so you can include this in another project.

To get your blog set up, clone this repo somewhere on your computer. Then choose a directory on your computer where you will put your markdown files (we'll call this the hotfolder). Then go to your terminal and run the following:

```bash
cd /location/of/dropblog/
bin/setup -d ~/Dropbox/blog/
lein run
```

Once the server starts, navigate to [localhost:8080](http://localhost:8080). You will see a mostly blank page. Then, go into your hotfolder and create a markdown file there (make sure its extension is either `md` or `markdown`). Wait a few moments and refresh the page in your browser. You will know the server processed the file properly when you see it renamed to something like `2012-07-04-your-file-name.md`.

Once you see the post appear on the web page successfully, reopen the markdown file (make sure you open the renamed file, not the old one which may still be in your editor). You will see it added some json at the top of the file like this:

```markdown
<!--{
	"created" : "2012-07-05T22:55:02.653Z"
}-->
```

This is how Dropblog keeps track of exactly what order the posts go in. You can add author information here as well:

```markdown
<!--{
	"created" : "2012-07-05T22:55:02.653Z",
	"author" : "Paul Harper"
}-->
```

You can get fancier than that:

```markdown
<!--{
	"created" : "2012-07-05T22:55:02.653Z",
	"author" : {
		"name" : "Paul Harper",
		"web" : "www.mysite.com",
		"email" : "me@mail.com"
	}
}-->
```

Your byline will change depending on what you include.

## License

Copyright (C) 2012 Paul Harper

Distributed under the [MIT license](http://www.opensource.org/licenses/MIT).
