// TODO: onload function should retrieve the data needed to populate the UI

document.addEventListener('DOMContentLoaded', () => {
//retrieve data
  fetch('http://localhost:8080/spamDetector-1.0/api/spam')
    .then(response => response.json())
    .then(data => {
      data.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>${item.filename}</td>
          <td>${item.spamProbability}</td>
          <td>${item.actualClass}</td>
        `;
        document.querySelector('#resultsTable tbody').appendChild(tr);
      });

      // After populating the table, calculate the width of the header cells
      const table = document.querySelector('#resultsTable');
      const tbody = table.querySelector('tbody');
      const thead = table.querySelector('thead');
      const tr = thead.querySelector('tr');
      const ths = tr.querySelectorAll('th');
      const tds = tbody.querySelector('tr').querySelectorAll('td');
      tds.forEach((td, i) => {
        const width = td.offsetWidth + 15;
        td.style.width = `${width}px`;
        ths[i].style.width = `${width}px`;
        console.log(width);
      });
    })
    .catch(() => {
      alert('Error retrieving data');
    });
});

fetch('http://localhost:8080/spamDetector-1.0/api/spam/accuracy')
  .then(response => response.json())
  .then(data => {
    // Display accuracy data
    document.querySelector('#accuracy').innerHTML = data.accuracy;
  })
  .catch(error => {
    alert('Error retrieving accuracy data');
  });

fetch('http://localhost:8080/spamDetector-1.0/api/spam/precision')
  .then(response => response.json())
  .then(data => {
    // Display precision data
    document.querySelector('#precision').innerHTML = data.precision;
  })
  .catch(error => {
    alert('Error retrieving precision data');
  });
