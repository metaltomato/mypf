/*!
* Start Bootstrap - Simple Sidebar v6.0.3 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2021 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
// 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
        const sidebarToggle = document.body.querySelector('#sidebarToggle');
        if (sidebarToggle) {
            // Uncomment Below to persist sidebar toggle between refreshes
            // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
            //     document.body.classList.toggle('sb-sidenav-toggled');
            // }
            sidebarToggle.addEventListener('click', event => {
                event.preventDefault();
                document.body.classList.toggle('sb-sidenav-toggled');
                localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
            });
        }
    const Type = (words, elementId) => {
      const htmlDom = document.getElementById(elementId);
      const delay = (time) => {
        return new Promise((resolve) => {
          setTimeout(() => {
            resolve("termino delay tiempo: " + time);
          }, time);
        });
      };
      const eraser = (htmlDom) => {
        return new Promise(async (resolve) => {
          const wordOriginalLength = htmlDom.innerText.length;
          for (let i = 0; i < wordOriginalLength; i++) {
            await delay(50);
            let erasedWord = htmlDom.innerText.slice(0, -1);
            htmlDom.innerText = erasedWord;
            if (wordOriginalLength - 1 === i) {
              resolve(true);
            }
          }
        });
      };
      const blinking = (htmlDom, times) => {
        return new Promise(async (resolve) => {
          for (let i = 0; i < times; i++) {
            await delay(500);
            let content = htmlDom.innerText.split("");
            content.at(-1) === "|"
              ? (htmlDom.innerText = htmlDom.innerText.slice(0, -1))
              : (htmlDom.innerText += "|");
            if (i === times - 1) {
              const r = await eraser(htmlDom);
              resolve(true);
            }
          }
        });
      };
      const writter = (word) => {
        return new Promise(async (resolve) => {
          const wordArray = word.split("");
          let storeWord = "";
          for (let i = 0; i <= wordArray.length; i++) {
            await delay(50);
            storeWord += wordArray[i];
            if (i === wordArray.length) {
              const r = await blinking(htmlDom, 5);
              resolve(true);
            } else {
              htmlDom.innerText = storeWord + "|";
            }
          }
        });
      };
      const init = (words) => {
        return new Promise(async (resolve) => {
          for (let i = 0; i < words.length; i++) {
            const r = await writter(words[i]);
            if (i === words.length - 1) {
              resolve(true);
            }
          }
        });
      };
      const loop = async (words) => {
        while (1 === 1) {
          await init(words);
        }
      };
      loop(words);
    };
    Type(
      [
        "스프링부트 3.0",
        "마리아 DB",
        "인텔리 J 로 제작되었습니다."
      ],
      "resume-subtitle"
    );

});
