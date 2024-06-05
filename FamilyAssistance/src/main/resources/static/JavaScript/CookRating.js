// 獲取當前網頁 URL
var currentURL = window.location.href;
// 提取根目錄
var rootURL = currentURL.split('/').slice(0, 3).join('/');
var wisher = document.getElementById('wisher').innerHTML
var title = document.getElementById('title').innerHTML
var dateStrgin = document.getElementById('dateString').innerHTML
var targetURL = rootURL+"/service/ShowCookingList/"+wisher

const mealStar = document.querySelector('.mealRating').children
const dishRatingContainers = document.querySelectorAll('.dishRating');
let mealRate = 3
let dishNum = 0;

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
		
		console.log(`meal rating: ${mealRating}`);
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
            console.log(`第 ${idx+1}道菜，Dish rating: ${rating}`);
        });
    }
});
