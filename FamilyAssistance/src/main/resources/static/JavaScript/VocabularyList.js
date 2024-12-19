// 函數：從隱藏欄位取得資料
function getWordDataFromHiddenDiv() {
    const hiddenDiv = document.getElementById("voc-data");
    const mapData = hiddenDiv.textContent || hiddenDiv.innerText; // 獲取 div 的內容

    console.log("獲取的 Map Data:", mapData);

    if (!mapData || mapData.trim() === "") {
        console.error("hiddenDiv 為空，無法解析");
        return [];
    }

    try {

    // 修正 Map 格式為 JSON 格式
    const jsonData = mapData
        .replace(/=([^,]+)/g, '="$1"') // 匹配等號後的所有值，直到下一個逗號
        .replace(/([0-9]+)=/g, '"$1":') // 將數字鍵加上雙引號
        .replace(/=/g, ':') // 將等號轉換為 JSON 冒號
        .replace(/, /g, ',') // 去掉逗號後的多餘空格
        .replace(/^"|"$/g, '')
        .replace(/}$/, '"}');  // 在最右邊的 `}` 左側插入一個雙引號
        
        console.log("轉換後的 JSON 字串:", `${jsonData}`);

        // 嘗試解析為 JSON
        const vocDictionary = JSON.parse(`${jsonData}`);

        // 將 JSON 字典轉換為陣列格式 [{ id: "1", name: "Ambiguous" }, ...]
        return Object.entries(vocDictionary).map(([id, word]) => ({ id, name: word }));
    } catch (error) {
        console.error("無法解析 Map 為 JSON：", error);
        return [];
    }
}



// 函數：生成表格
function generateTable(data) {
    const tableContainer = document.getElementById("table-container");
    tableContainer.innerHTML = ""; // 清空容器內容

    // 建立表格元素
    const table = document.createElement("table");

    // 建立表頭
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    ["單字名稱", "查詢", "刪除"].forEach((headerText) => {
        const th = document.createElement("th");
        th.textContent = headerText;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // 建立表格內容
    const tbody = document.createElement("tbody");
    data.forEach((word) => {
        const row = document.createElement("tr");

        // 單字名稱
        const nameCell = document.createElement("td");
        nameCell.textContent = word.name;
        row.appendChild(nameCell);

        // 查詢按鈕
        const queryCell = document.createElement("td");
        const queryButton = document.createElement("button");
        queryButton.textContent = "查詢";
        queryCell.appendChild(queryButton);
        queryButton.classList.add("button");
        row.appendChild(queryCell);

        // 刪除按鈕
        const deleteCell = document.createElement("td");
        const deleteButton = document.createElement("button");
        deleteButton.textContent = "刪除";
        deleteCell.appendChild(deleteButton);
        deleteButton.classList.add("button");
        row.appendChild(deleteCell);

        tbody.appendChild(row);
    });
    table.appendChild(tbody);

    // 將表格插入容器
    tableContainer.appendChild(table);
}

// 初始化
document.addEventListener("DOMContentLoaded", () => {
    const wordData = getWordDataFromHiddenDiv();
    console.log("解析後的資料:", wordData);

    // 若需要生成表格，可以調用 generateTable
    generateTable(wordData);
});
