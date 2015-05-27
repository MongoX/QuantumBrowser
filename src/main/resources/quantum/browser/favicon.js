window.cefQuery({
    request: 'favicon:\001' + location.href + '\001' + (function () {
        var favicon = '/favicon.ico';
        var nodeList = document.getElementsByTagName("link");
        for (var i = 0; i < nodeList.length; i++)
            if ((nodeList[i].getAttribute("rel") == "icon") || (nodeList[i].getAttribute("rel") == "shortcut icon"))
                favicon = nodeList[i].getAttribute("href");
        return favicon;
    })()
});
