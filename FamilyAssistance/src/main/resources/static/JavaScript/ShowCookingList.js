// 獲取當前網頁 URL
var currentURL = window.location.href;
// 提取根目錄
var rootURL = currentURL.split('/').slice(0, 3).join('/');
var wisher = document.getElementById('wisher').innerHTML
var title = document.getElementById('title').innerHTML
var targetURL = rootURL+"/service/ShowCookingList/"+wisher

//console.log("根目錄 URL:", rootURL);
//console.log("wisher:"+wisher);
//console.log('title:'+title)
getCookingList()


async function getCookingList(){
	const res = await fetch(targetURL)
	const data = await res.json()

    var YearList = data.reduce(function(acc, curr) {
    // 從日期中提取年份
    var year = (new Date(curr.cookDate)).getFullYear();
  
    // 如果 acc 中已經有該年份的資料陣列，則將目前的資料加入其中
    if(acc[year]) {
      acc[year].push(curr);
    } else {
      // 否則，創建一個新的資料陣列
      acc[year] = [curr];
    }
    return acc;
  }, {});
  //console.log(YearList);

  const years = Object.keys(YearList);
  years.forEach(year =>{
	  
	//創建年度container  
	const yearContainer = document.createElement('div');
    yearContainer.classList.add('Year-container');  
    
    // 創建 <h2> 年度標籤
    const h2 = document.createElement('h2');
    h2.textContent = year+" 年度"; 
    yearContainer.appendChild(h2);    
    //console.log("年份:", year);
    const dataArray = YearList[year];
    
  
    // 創建日期物件，用於存放按日期分類的資料
    const dateMap = {};
  
    dataArray.forEach(cook => {
        // 取得資料中的日期
        var cookDate = new Date(cook.cookDate);
        var cookMonth = cookDate.getMonth() + 1;
        var cookDay = cookDate.getDate();
        var weekday = ['日', '一', '二', '三', '四', '五', '六'][cookDate.getDay()];
  
        // 格式化日期為 mm/dd(星期)
        var formattedDate = cookMonth.toString().padStart(2, '0') + '/' + cookDay.toString().padStart(2, '0') + '(' + weekday + ')';
  
        // 將資料按日期分類存入對應的日期物件中
        if (!dateMap[formattedDate]) {
            dateMap[formattedDate] = [];
        }
        dateMap[formattedDate].push(cook);
    });
    
    //console.log(dateMap)
    //=================
    //第二層資料
    //=================
    for (const [date, cooks] of Object.entries(dateMap)){
        // 創建 <div> 元素
        let isCreate = false
        var cookInfoDiv = document.createElement('div');
        cookInfoDiv.classList.add('cookInfo');
        cookInfoDiv.id = 'cookInfo';
        cookInfoDiv.innerText = date;
  
        //修改資料
        let RateDate = year+date.replace(/\//g, "").replace(/\(|\)/g, "").substring(0, 4);
        // 創建評分button
        var rateBtn = document.createElement('button');
        rateBtn.innerText = "評分";
        rateBtn.classList.add('btn');
        rateBtn.addEventListener('click', ()=>redirectToRatePage(RateDate,wisher));
  
        // 創建上傳圖片btn
        var updateBtn = document.createElement('button');
        updateBtn.innerText = "上傳圖片";
        updateBtn.classList.add('btn');
        updateBtn.addEventListener('click', ()=>redirectUpdateToRatePage);
  
        //創建表格
        const table = document.createElement('table');
        table.id = 'cooktable';

        // 創建表頭
        const tableHead = document.createElement('thead');
        const headerRow = document.createElement('tr');
        let headers;
        if(title.includes("Me")){
			 headers = ['', '',''];
		}else{
			 headers = ['','', ''];
		}

        headers.forEach(headerText => {
	    
        const th = document.createElement('th');
        th.textContent = headerText;
        headerRow.appendChild(th);
        });

        tableHead.appendChild(headerRow);
        table.appendChild(tableHead);

         // 創建表身
        const tableBody = document.createElement('tbody');

        // 將每個菜的資料填入表格
        cooks.forEach(cook => {
        const row = document.createElement('tr');

        // 如果 title 不包含 "Me"，则添加跨列的图片单元格
        if (!title.includes("Me") && isCreate==false) {
        const imgCell = document.createElement('td');
        imgCell.rowSpan = cooks.length; // 设置跨越的行数为 cooks 的长度
        const img = document.createElement('img');
        img.classList.add('img');
        img.src = '../CookPic/NoPic.png'; // 设置图片的 URL
        img.alt = 'Image';
        imgCell.appendChild(img);
        row.appendChild(imgCell);
        isCreate = true;
        }

        // 菜名
        const nameCell = document.createElement('td');
        nameCell.textContent = cook.cookName;
        row.appendChild(nameCell);

        // 類型
        const typeCell = document.createElement('td');
        typeCell.textContent = cook.type;
        row.appendChild(typeCell);
        
        //修改按鈕
        if(title.includes("Me")){
	       const modifyCell = document.createElement('td');
	       const modifyBtn = document.createElement('button');
	       modifyBtn.innerText = "修改";
           modifyBtn.classList.add('btn');
           modifyBtn.addEventListener('click', ()=>redirectmodifyCookList(cook.uuid,wisher));
           modifyCell.appendChild(modifyBtn);
           row.appendChild(modifyCell);
		}else{
			
		}

        tableBody.appendChild(row);
        });

        table.appendChild(tableBody);
  
        // 將 <h2> 、rateBtn 添加到 cookInfoDiv
        if(title.includes("Me")){
		    //如果是我的葉面就不能評分
		}else{
        cookInfoDiv.appendChild(rateBtn);
        }
        cookInfoDiv.appendChild(updateBtn);
        yearContainer.appendChild(cookInfoDiv);
        yearContainer.appendChild(table);
	}
	
	
    const mainDiv = document.getElementById('main');
    mainDiv.appendChild(yearContainer);

  })

}


function redirectToRatePage(date,wisher){
	 window.location.href = rootURL+"/RatingCook/"+wisher+"/"+date;
}

function redirectUpdateToRatePage(){
	window.location.href = "#";
}

function redirectmodifyCookList(id,wisher){
	window.location.href = rootURL+"/CookModify/"+id+"/"+wisher;
}
