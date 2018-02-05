
function findUrl(imgUrl) {
    var imgList = document.getElementsByTagName('img');
    alert('123');
    for (var i = 0; i < imgList.length; i++) {
        var imgItem = imgList[i];
        if (imgUrl.indexOf(imgItem.src) > -1) {
            imgItem.style.display = 'none';
            break;
        }
    }
}