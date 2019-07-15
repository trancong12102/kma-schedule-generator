/*
  author: lonewolf2110
*/

import Swal from 'sweetalert2'
import axios from 'axios'

//-----------------------------------------------------------------------------
// event listener
HTMLElement.prototype.handle = function (eventName, callback) {
  this.addEventListener(eventName, callback);
};

//-----------------------------------------------------------------------------
/// Main

// Loader
setTimeout(() => {
  $('#pb_loader').classList.remove('show');
}, 700);

// Handle animation
handleAppearAnimation();

// Select all text when active
$("#username").handle('focus', function () {
  this.select();
});

$("#password").handle('focus', function () {
  this.select();
});

// Handle form submit
const COLOR_PRIMARY = '#5f4d93';
const COLOR_RED = '#e37682';

$("#login-form").handle("submit", (e) => {
  const username = $('#username').value.trim().toUpperCase();
  const password = $('#password').value.trim();
  let inputOptions = {
    'post': 'Tạo thời khoá biểu',
    'get': 'Xem thời khoá biểu đã tạo',
    'j4f': 'Hack nick facebook crush'
  };

  if (password === "") {
    inputOptions = {
      'get': 'Xem thời khoá biểu đã tạo',
      'j4f': 'Hack nick facebook crush'
    };
  }

  Swal.fire({
    type: 'info',
    title: 'hihi',
    input: 'select',
    showCancelButton: true,
    cancelButtonText: 'Quay lại',
    cancelButtonColor: COLOR_RED,
    confirmButtonText: 'Tiếp tục',
    confirmButtonColor: COLOR_PRIMARY,
    reverseButtons: true,
    inputOptions: inputOptions,
    showLoaderOnConfirm: true,
    allowOutsideClick: () => !Swal.isLoading(),
    allowEscapeKey: () => !Swal.isLoading(),
    inputValidator: (value) => {
      return new Promise((resolve) => {
        if (value === 'j4f') {
          resolve('<span class="pb_dog fas fa-dog"></span> <span class="pb_dog fas fa-bone"></span> <span class="pb_dog fas fa-dog"></span> Ahihi đồ chó =)) <span class="pb_dog fas fa-bone"></span> <span class="pb_dog fas fa-dog"></span> <span class="pb_dog fas fa-bone"></span>')
        } else {
          resolve()
        }
      })
    },
    preConfirm: (method) => {
      let axiosData = {
        method: method,
        url: 'schedule',
        data: {
          username: username,
          password: password
        }
      };

      if (method === "get") {
        axiosData = {
          method: method,
          url: `schedule?username=${username}`
        }
      }

      return axios(axiosData)
        .then(response => {

          return response;
        })
        .catch(error => {

          return error.response;
        })
    },
  }).then(result => {
    if (result.value) {
      const response = result.value;

      if (response.status === 204) {
        handleAjaxWarning();
        return;
      }

      if (response.status === 200) {
        handleAjaxSuccess(response);
      } else {
        handleAjaxFailed(response);
      }
    }
  });

  e.preventDefault();
});

//------------------------------------------------------------------------------------------------------
function handleAjaxWarning() {
  Swal.fire({
    type: 'warning',
    title: 'Warning',
    text: 'Bạn chưa đăng ký học cho học kỳ này',
    confirmButtonColor: COLOR_RED,
    confirmButtonText: 'Đóng',
  });
}

function handleAjaxSuccess(response) {
  Swal.fire({
    type: 'success',
    title: 'Thành công',
    text: 'Bạn sẽ đuợc chuyển huớng đến thư mục chứa thời khoá biểu sau khi đóng thông báo này',
    confirmButtonColor: COLOR_PRIMARY,
    confirmButtonText: 'Đóng',
  }).then(() => {
    new Promise(() => {
      setTimeout(() => {
        const url = response.data.webViewLink;
        window.open(url, "_blank");
      }, 500);
    });
  });
}

function handleAjaxFailed(response) {
  const status = response.status;
  let text = 'Lỗi không xác định!<br>Vui lòng thử lại sau';

  switch (status) {
    case 400:
      text = 'Bad request';
      break;
    case 401:
      text = 'Sai tên đăng nhập hoặc mật khẩu';
      break;
    case 404:
      text = 'Thời khoá biểu của bạn không tồn tại trong hệ thống<br>Vui lòng tạo thời khoá biểu';
      break;
    case 503:
      text = 'Server của truờng đang gặp sự cố!<br>Vui lòng thử lại sau';
      break;
    case 500:
      text = 'Server đang gặp sự cố!<br>Vui lòng thử lại sau';
      break;
  }

  Swal.fire({
    type: 'error',
    title: 'Lỗi (>.<)',
    html: text,
    confirmButtonColor: COLOR_RED
  })
}

//------------------------------------------------------------------------------------------------------
/// No need jQuery

// jQuery style HTML Element selector
function $(selector) {
  let selectorString = selector.substr(1, selector.length - 1);

  return selector[0] === "#"
    ? document.getElementById(selectorString) : selector[0] === "."
      ? document.getElementsByClassName(selectorString) : document.getElementsByTagName(selector);
}

// Query Selector all
function $q(selector) {
  return document.querySelectorAll(selector);
}

// Handle animation functions
function addAnimation(element, animation, hover = false) {
  if (hover) {
    element.classList.forEach(value => {
      if (value.startsWith("pb_animated_")) {
        element.classList.remove(value);
      }
    });

    element.classList.add("pb_animated_hover");
  }

  element.classList.add('animated', animation);

  function handleAnimationEnd() {
    element.classList.remove('animated', animation);
    element.removeEventListener('animationend', handleAnimationEnd);
  }

  element.addEventListener('animationend', handleAnimationEnd);
}

function handleAppearAnimation() {
  const PREFIX = 'pb_animated_';

  $q('.pb_animated').forEach(element => {
    let animation = '';
    let hover = false;

    element.classList.forEach(value => {
      if (value.indexOf(PREFIX) !== -1) {
        animation = value.replace(PREFIX, '');
      }

      if (value === "pb_hover") {
        hover = true;
      }

    });

    addAnimation(element, animation);

    if (hover) {
      element.addEventListener("mouseover", () => {
        addAnimation(element, animation, true);
      })
    }

  });
}

