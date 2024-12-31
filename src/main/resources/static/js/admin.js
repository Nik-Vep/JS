
let tableUsers = [];
let currentUser = "";

document.addEventListener('DOMContentLoaded', () => {
    getUsers();
    getCurrentUser();
});

function getUsers() {
    fetch("/api/admin/")
        .then(res => res.json())
        .then(data => {
            tableUsers = data.length > 0 ? data : [];
            showUsers(tableUsers);
        })
        .catch(error => console.error('Error fetching users:', error));
}

function getCurrentUser() {
    fetch("/api/admin/current")
        .then(res => res.json())
        .then(data => {
            currentUser = data;
            console.log(data);
            showUserRoles(currentUser);
            showUserEmail(currentUser);
            showOneUser(currentUser);
        })
        .catch(error => console.error('Error fetching current user:', error));
}

function showUsers(table) {
    let temp = "";
    table.forEach(user => {
        temp += "<tr>";
        temp += `<td>${user.id}</td>`;
        temp += `<td>${user.username}</td>`;
        temp += `<td>${user.surname}</td>`;
        temp += `<td>${user.department}</td>`;
        temp += `<td>${user.salary}</td>`;
        temp += `<td>${user.roles.map(role => role.name.substring(5)).join(", ")}</td>`;
        temp += `<td>${user.email}</td>`;
        temp += `<td><button onclick='showEditModal(${user.id})' class="btn btn-info">Edit</button></td>`;
        temp += `<td><button onclick='showDeleteModal(${user.id})' class="btn btn-danger">Delete</button></td>`;
        temp += "</tr>";
    });
    document.getElementById("allUsersBody").innerHTML = temp;
}

function showOneUser(user) {
    let temp = "<tr>";
    temp += `<td>${user.id}</td>`;
    temp += `<td>${user.username}</td>`;
    temp += `<td>${user.surname}</td>`;
    temp += `<td>${user.department}</td>`;
    temp += `<td>${user.salary}</td>`;
    temp += "</tr>";
    document.getElementById("oneUserBody").innerHTML = temp;
}

function showUserRoles(user) {
    let roles = user.roles.map(role => role.authority).join(", ");
    document.getElementById("userRoles").innerText = roles;
}


function showUserEmail(user) {
    let email = user.email;
    document.getElementById("userEmail").innerText = email;
}

function getSelectedRoles(selector) {
    let roles = [];
    let sel = document.querySelector(selector);
    if (sel) {
        Array.from(sel.options).forEach(option => {
            if (option.selected) {
                roles.push(parseLong(option.value));
            }
        });
    }
    return roles;
}

function parseLong(value) {
    return parseInt(value, 10);
}

document.getElementById('newUserForm').addEventListener('submit', addNewUser);

function addNewUser(event) {
    event.preventDefault();
    let newUserForm = new FormData(event.target);
    let user = {
        username: newUserForm.get('username'),
        surname: newUserForm.get('surname'),
        department: newUserForm.get('department'),
        salary: parseLong(newUserForm.get('salary')),
        password: newUserForm.get('password'),
        email: newUserForm.get('email')
    };
    let rolesId = getSelectedRoles("#roles");

    let params = new URLSearchParams();
    rolesId.forEach(roleId => params.append('rolesId', roleId));
    let url = `/api/admin/?${params.toString()}`;

    let request = new Request(url, {
        method: 'POST',
        body: JSON.stringify(user),
        headers: {
            'Content-Type': 'application/json'
        }
    });

    fetch(request)
        .then(response => {
            if (response.ok) {
                getUsers();
                event.target.reset();
                $('.nav-tabs a[href="#nav-home"]').tab('show');
            } else {
                return response.json().then(err => { throw err; });
            }
        })
        .catch(error => console.error('Error adding user:', error));
}

function showDeleteModal(id) {
    let user = tableUsers.find(u => u.id === id);
    if (!user) {
        console.error(`User with ID ${id} not found`);
        return;
    }

    document.getElementById('idDel').value = user.id;
    document.getElementById('usernameDel').value = user.username;
    document.getElementById('surnameDel').value = user.surname;
    document.getElementById('departmentDel').value = user.department;
    document.getElementById('salaryDel').value = user.salary;
    document.getElementById('emailDel').value = user.email;

    let rolesDelElement = document.getElementById('rolesDel');
    Array.from(rolesDelElement.options).forEach(option => {
        option.selected = false;
    });

    user.roles.forEach(role => {
        let option = rolesDelElement.querySelector(`option[value="${role.id}"]`);
        if (option) {
            option.selected = true;
        }
    });

    $('#deleteModal').modal('show');
}

document.getElementById('deleteUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    let id = document.getElementById('idDel').value;

    let request = new Request(`/api/admin/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    fetch(request)
        .then(response => {
            if (response.ok) {
                getUsers();
                $('#deleteModal').modal('hide');
            } else {
                return response.json().then(err => { throw err; });
            }
        })
        .catch(error => console.error('Error deleting user:', error));
});

function showEditModal(id) {
    let user = tableUsers.find(u => u.id === id);
    if (!user) {
        console.error(`User with ID ${id} not found`);
        return;
    }

    document.getElementById('idRed').value = user.id;
    document.getElementById('usernameRed').value = user.username;
    document.getElementById('surnameRed').value = user.surname;
    document.getElementById('departmentRed').value = user.department;
    document.getElementById('salaryRed').value = user.salary;
    document.getElementById('passwordRed').value = "";
    document.getElementById('emailRed').value = user.email;

    let rolesRedElement = document.getElementById('rolesRed');
    Array.from(rolesRedElement.options).forEach(option => {
        option.selected = false;
    });

    user.roles.forEach(role => {
        let option = rolesRedElement.querySelector(`option[value="${role.id}"]`);
        if (option) {
            option.selected = true;
        }
    });

    $('#editModal').modal('show');
}

document.getElementById('editUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    let editUserForm = new FormData(event.target);
    let password = editUserForm.get('password').trim();
    let user = {
        id: parseLong(editUserForm.get('id')),
        username: editUserForm.get('username'),
        surname: editUserForm.get('surname'),
        department: editUserForm.get('department'),
        salary: parseLong(editUserForm.get('salary')),
        email: editUserForm.get('email')
    };

    if (password !== "") {
        user.password = password;
    }

    let rolesId = getSelectedRoles("#rolesRed");

    let params = new URLSearchParams();
    rolesId.forEach(roleId => params.append('rolesId', roleId));
    let url = `/api/admin/${user.id}?${params.toString()}`;

    let request = new Request(url, {
        method: 'PUT',
        body: JSON.stringify(user),
        headers: {
            'Content-Type': 'application/json',
        },
    });

    fetch(request)
        .then(response => {
            if (response.ok) {
                getUsers();
                $('#editModal').modal('hide');
            } else {
                return response.json().then(err => { throw err; });
            }
        })
        .catch(error => console.error('Error editing user:', error));
});
