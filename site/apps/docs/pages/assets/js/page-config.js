const configElement = document.querySelector("configure");

const hideToc = !!configElement.attributes.getNamedItem("hideToc");
const contentWidth = hideToc && configElement.attributes.getNamedItem("contentWidth");

if (hideToc) {
  const containerRow = document.querySelector("body > .container > .row");
  const tocCol = containerRow.querySelector("& > div:first-child");
  tocCol.style.visibility = "collapse";
}

if (contentWidth) {
  const containerRow = document.querySelector("body > .container > .row");
  const contentCol = containerRow.querySelector("& > div:nth-child(2)");

  switch (contentWidth.value) {
    case "center": {
      contentCol.classList.remove("col-md-9");
      contentCol.classList.add("col-md-6");
      break;
    }
    case "stretch": {
      contentCol.classList.remove("col-md-9");
      contentCol.classList.add("col-md-12");

      const tocCol = containerRow.querySelector("& > div:first-child");
      tocCol.hidden = true;
      break;
    }
  }
}
