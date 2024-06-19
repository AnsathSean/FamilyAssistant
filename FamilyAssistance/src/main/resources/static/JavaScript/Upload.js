// 獲取當前網頁 URL
var currentURL = window.location.href;
// 提取根目錄
var rootURL = currentURL.split('/').slice(0, 3).join('/');
var wisher = document.getElementById('wisher').innerHTML
var dateString = document.getElementById('dateString').innerHTML
var GetBentotURL = rootURL+"/service/BentoInfo/"+wisher+"/"+dateString
var Purl = rootURL+"/service/AddBento/"
const dishRatingContainers = document.querySelectorAll('.dishRating');
let mealRate = 3
let dishNum = 0;


 document.addEventListener("DOMContentLoaded", async function() {
	       const res = await fetch(GetBentotURL) 
           const data = await res.json()
           console.log(data)
            // Convert dateString to desired format and set to header
            const dateString = data.dateString;
            const year = dateString.slice(0, 4);
            const month = dateString.slice(4, 6);
            const day = dateString.slice(6, 8);
            const formattedDate = `${year}年${month}月${day}日 (${new Date(year, month - 1, day).toLocaleDateString('zh-TW', { weekday: 'short' })})`;
            document.getElementById('cookDate').innerText = formattedDate;

            const bentoID = document.getElementById('bentoID')
            bentoID.innerHTML = data.bentoID
})

 function uploadFile() {
	 console.log("我有執行上傳")
      var formData = new FormData();
      var fileInput = document.getElementById('fileInput');
      formData.append('file', fileInput.files[0]);

      var xhr = new XMLHttpRequest();
      const bentoID = document.getElementById('bentoID').innerHTML
      
      xhr.open('POST', rootURL+'/service/uploadfile/'+bentoID, true);

      xhr.onload = function () {
          if (xhr.status === 200) {
              alert('File uploaded successfully');
          } else {
              alert('File upload failed');
          }
      };

      xhr.send(formData);
  }