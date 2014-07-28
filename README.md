Android-Pulltorefresh
=====================

Gmail like pulltorefresh w/o breaking touch interactions

Of course, ListView is one of the mostly used GUI structure for displaying list of scrollable items, and Android Developers have put a lot of effort to make it beautiful, responsive, and memory efficient. Pull To Refresh is/was innovative and sleek UX design to refresh the list data. And it's Twitter who rendered this new way of refreshing list data first, instead of doing a button click. And it became viral, got a lot of attention. Twitter patented the gesture in 2010, in year 2013 Twitter officially announced that the patent will be used only for defensive purpose, anyone using it can continue the usage without any issues. This year Google updated their Apps (Gmail, G+) and integrated their own, a variant of pull to refresh design. But its touch interaction is broken in a way. You can't do pull to refresh while you are scrolling the list. List's first item needs to be positioned at the top to occur the pull to refresh gesture. ie, First you have to scroll the list to top, leave the touch (scroll gesture) and then start the pulling. 

This sample code will help you write custom pulltorefresh without breaking touch interactions. !!!
