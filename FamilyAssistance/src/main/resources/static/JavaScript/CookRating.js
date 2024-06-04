// 獲取當前網頁 URL
var currentURL = window.location.href;
// 提取根目錄
var rootURL = currentURL.split('/').slice(0, 3).join('/');
var wisher = document.getElementById('wisher').innerHTML
var title = document.getElementById('title').innerHTML
var dateStrgin = document.getElementById('dateString').innerHTML
var targetURL = rootURL+"/service/ShowCookingList/"+wisher