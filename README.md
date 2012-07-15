# Dropblog

A simple blog engine written in clojure and [noir](http://webnoir.org). Just drop a markdown file into your dropbox folder, and watch your post go live.

## Getting Started

This is a library that currently is build specifically to work with [noir](http://webnoir.org/). To use it, simply add it as a dependency in your `project.clj` file:

```clojure
[benekastah/dropblog "0.1.0-SNAPSHOT"]
```

Then, create a directory under `resources/posts` (or, in order to make it work from Dropbox, symlink a folder inside your Dropbox folder into `resources/posts`). This will act as a hotfolder: any markdown file placed in this folder will turn into a blog post within 10 or so seconds.

To make this work, add a small bit of code to your `server.clj` file:

```
(ns myblog.server
  (:require [noir.server :as server]
            [dropblog.core :as dropblog]))

(dropblog/initialize :default-author-name "Block Design")
```

This will initialize the options you pass in and start the process that looks for new posts.

Then you just need to define a couple simple routes, one for the blog stream and the other for a single post:

```clojure
(ns myblog.views.home
  (:require [dropblog.views [post-stream :as post-stream]
                             [post :as post]])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.core :only [html5]]))

(defpartial base [& content]
  (html5
    [:head
      [:title "My Blog"]]
    [:body
      [:h1 "My Blog"]
      [:div content]]))

(defpage "/" []
  (base
    [:h1 "Block Blog"]
    ;; Simply pass in the maximum number of posts you with to display per page
    (post-stream/stream 50)))

(defpage "/post/:year/:month/:day/:title" {:keys [year month day title]}
  (base
    (post/get-post year month day title)))
```

Then run `lein run`. Once the server starts, navigate to [localhost:8080](http://localhost:8080). Go into `resources/posts` and create a markdown file there (make sure its extension is either `md` or `markdown`). Wait a few moments and refresh the page in your browser. You will know the server processed the file properly when you see it renamed to something like `2012-07-04-your-file-name.md`.

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
