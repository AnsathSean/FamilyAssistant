// 獲取當前網頁 URL
var currentURL = window.location.href;
// 提取根目錄
var rootURL = currentURL.split('/').slice(0, 3).join('/');
var wisher = document.getElementById('wisher').innerHTML
var title = document.getElementById('title').innerHTML
var dateString = document.getElementById('dateString').innerHTML
var targetURL = rootURL+"/service/BentoInfo/"+wisher+"/"+dateString
var Purl = rootURL+"/service/AddBento/"
const mealStar = document.querySelector('.mealRating').children
const dishRatingContainers = document.querySelectorAll('.dishRating');
let mealRate = 3
let dishNum = 0;




 document.addEventListener("DOMContentLoaded", async function() {
	       const res = await fetch(targetURL) 
           const data = await res.json()
           //console.log(data)
            // Convert dateString to desired format and set to header
            const dateString = data.dateString;
            const year = dateString.slice(0, 4);
            const month = dateString.slice(4, 6);
            const day = dateString.slice(6, 8);
            const formattedDate = `${year}年${month}月${day}日 (${new Date(year, month - 1, day).toLocaleDateString('zh-TW', { weekday: 'short' })})`;
            document.getElementById('cookDate').innerText = formattedDate;

            // Set bento rate stars
            const bentoRateContainer = document.getElementById('bentoRate');
            setRatingStars(bentoRateContainer, data.bentoRate);
            const bentoID = document.getElementById('bentoID')
            bentoID.innerHTML = data.bentoID
             //填寫評論內容
            const comment = document.getElementById('inputText')
            comment.textContent = data.comment


        // Sort cooks based on type and cookName
        const sortedCooks = data.cooks.sort((a, b) => {
            if (a.type === "主菜") return -1;
            if (b.type === "主菜") return 1;
            if (a.cookName.includes("蛋") && a.type === "配菜") return 1;
            if (b.cookName.includes("蛋") && b.type === "配菜") return -1;
            if (a.type === "蔬菜") return 1;
            if (b.type === "蔬菜") return -1;
            return 0;
        });

    // Add each meal to the cooking list
    const cookingListContainer = document.getElementById('cookingList');
    sortedCooks.forEach((cook, index) => {
        const mealDiv = document.createElement('div');
        mealDiv.classList.add('meal');
        mealDiv.innerHTML = `
            <p>${cook.cookName}</p>
            <div class="dishRating" id="dishRating-${index}"></div>
            <div class="hidden" id="GetdishRating-${index}">0</div>
            <div class="hidden" id="GetdishUUID-${index}">${cook.uuid}</div>
            
        `;
        cookingListContainer.appendChild(mealDiv);
        const dishRatingContainer = document.getElementById(`dishRating-${index}`);
        setRatingStars(dishRatingContainer, cook.rate);
    });


});




function setRatingStars(container, rate) {
    const fullStar = '<i class="fas fa-star"></i>';
    const emptyStar = '<i class="far fa-star"></i>';
    const rating = rate !== null ? rate : 0;
    for (let i = 0; i < 5; i++) {
        container.innerHTML += (i < rating) ? fullStar : emptyStar;
    }
}




for(let i=0;i< mealStar.length ;i++){
	mealStar[i].addEventListener('click',function(){
		const mealRating = i + 1;
		for(let j=0;j<mealStar.length;j++){
			mealStar[j].classList.remove("fas")
			mealStar[j].classList.add("far")
		}
		for(let j=0;j<=i;j++){
			mealStar[j].classList.remove("far") //先移除空心的星星
            mealStar[j].classList.add("fas") //添加新的星星 如果i=j表示選中的
		}
		
		//console.log(`meal rating: ${mealRating}`);
	})
}

// 对每个 dishRating 进行循环
dishRatingContainers.forEach((dishRating,idx) => {
    // 获取当前 dishRating 下的所有星星元素
    const dishStars = dishRating.children;
    // 对每颗星星添加点击事件监听器
    for (let i = 0; i < dishStars.length; i++) {
        dishStars[i].addEventListener('click', function () {
            // 根据点击的星星序号，设置相应的评分
            const rating = i + 1;

            // 移除所有星星的填充样式
            for (let j = 0; j < dishStars.length; j++) {
                dishStars[j].classList.remove("fas");
                dishStars[j].classList.add("far");
            }

            // 填充点击之前的所有星星
            for (let j = 0; j <= i; j++) {
                dishStars[j].classList.remove("far");
                dishStars[j].classList.add("fas");
            }

            // 在这里你可以执行其他操作，比如将评分保存到数据库等
            //console.log(`第 ${idx+1}道菜，Dish rating: ${rating}`);
        });
    }
});


function createBentoModel(wisher,bentoID, dateString, bentoRate, comment, cooks) {
    // 創建 Bento 對象
    const bento = {
        wisher: wisher,
        bentoID: bentoID, // 請根據後端需求填寫
        dateString: dateString,
        comment: comment,
        bentoRate: bentoRate,
        cooks: cooks
    };

    return bento;
}


function createCookModel(UUID, lineID, cookDate, cookTime, type, cookName, rate) {
	    // 將日期和時間格式化為 ISO 8601 格式
    const formattedCookDate = new Date(cookDate)
    const formattedCookTime = new Date(cookTime)
    // 創建 Cook 對象
    const cook = {
        uuid: UUID,
        lineID: lineID,
        cookDate: null,
        cookTime: null,
        type: type,
        cookName: cookName,
        rate: rate
    };

    return cook;
}
