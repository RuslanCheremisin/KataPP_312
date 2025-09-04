let currentUser = null;
let allUsers = [];
let allRoles = [];

document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

async function initializeApp() {
    try {
        await Promise.all([
            loadCurrentUser(),
            loadAllUsers(),
            loadAllRoles()
        ]);

        renderUserProfile();
        renderUsersTable();
        fillRoleSelects();
    } catch (error) {
        console.error('Ошибка инициализации:', error);
        showNotification('Ошибка загрузки данных', 'danger');
    }
}

function setupEventListeners() {
    document.getElementById('logoutForm').addEventListener('submit', handleLogout);

    document.getElementById('addUserForm').addEventListener('submit', handleAddUser);
    document.getElementById('editForm').addEventListener('submit', handleEditUser);
    document.getElementById('deleteForm').addEventListener('submit', handleDeleteUser);

    const editModal = document.getElementById('editModal');
    const deleteModal = document.getElementById('deleteModal');

    if (editModal) {
        editModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            if (button) {
                const userId = button.getAttribute('data-user-id');
                const user = allUsers.find(u => u.id == userId);
                if (user) {
                    fillEditForm(user);
                }
            }
        });

        editModal.addEventListener('hidden.bs.modal', resetEditForm);
    }

    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            if (button) {
                const userId = button.getAttribute('data-user-id');
                const user = allUsers.find(u => u.id == userId);
                if (user) {
                    fillDeleteForm(user);
                }
            }
        });
    }
}

// ===== CSRF TOKEN FUNCTIONS =====
function getCSRFToken() {
    // Spring Security хранит CSRF токен в cookie с именем 'XSRF-TOKEN'
    const name = 'XSRF-TOKEN=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');

    for (let i = 0; i < cookieArray.length; i++) {
        let cookie = cookieArray[i].trim();
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return null;
}

function getCSRFHeader() {
    const token = getCSRFToken();
    return token ? { 'X-XSRF-TOKEN': token } : {};
}

// ===== API CALL WITH CSRF =====
async function apiCall(url, options = {}) {
    try {
        const method = options.method ? options.method.toUpperCase() : 'GET';
        const isModifyingRequest = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method);

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        // Добавляем CSRF токен для модифицирующих запросов
        if (isModifyingRequest) {
            Object.assign(headers, getCSRFHeader());
        }

        const response = await fetch(url, {
            headers,
            credentials: 'include', // Важно для отправки cookies
            ...options
        });

        if (!response.ok) {
            // Если получили 403 - возможно CSRF токен невалиден
            if (response.status === 403) {
                showNotification('Ошибка доступа. Попробуйте перезагрузить страницу.', 'danger');
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.status === 204 ? null : await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
}

// ===== SPECIFIC API METHODS WITH CSRF =====
async function apiGet(url) {
    return apiCall(url, { method: 'GET' });
}

async function apiPost(url, data) {
    return apiCall(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

async function apiPut(url, data) {
    return apiCall(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

async function apiDelete(url) {
    return apiCall(url, { method: 'DELETE' });
}

async function loadCurrentUser() {
    currentUser = await apiGet('/admin/current-user');
}

async function loadAllUsers() {
    allUsers = await apiGet('/admin/users');
}

async function loadAllRoles() {
    allRoles = await apiGet('/admin/roles');
}

function renderUserProfile() {
    if (currentUser) {
        document.getElementById('currentUsername').textContent = currentUser.username;
        document.getElementById('currentUserRoles').textContent = 'Роли: ' + currentUser.roles.map(r => r.name).join(', ');

        document.getElementById('profileFirstName').textContent = currentUser.firstName;
        document.getElementById('profileLastName').textContent = currentUser.lastName || '-';
        document.getElementById('profileAge').textContent = currentUser.age;
        document.getElementById('profileUsername').textContent = currentUser.username;
    }
}

function renderUsersTable() {
    const tbody = document.getElementById('usersTableBody');
    tbody.innerHTML = '';

    allUsers.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName || '-'}</td>
            <td>${user.age}</td>
            <td>${user.username}</td>
            <td>${user.roles.map(r => r.name).join(', ')}</td>
            <td>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-warning me-1 rounded"
                            data-bs-toggle="modal" data-bs-target="#editModal"
                            data-user-id="${user.id}">
                        Изменить
                    </button>
                    <button type="button" class="btn btn-danger btn-sm rounded"
                            data-bs-toggle="modal" data-bs-target="#deleteModal"
                            data-user-id="${user.id}">
                        Удалить
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function fillRoleSelects() {
    const rolesSelect = document.getElementById('roles');
    const editRolesSelect = document.getElementById('editRoles');

    if (rolesSelect) rolesSelect.innerHTML = '';
    if (editRolesSelect) editRolesSelect.innerHTML = '';

    allRoles.forEach(role => {
        const option = document.createElement('option');
        option.value = role.id;
        option.textContent = role.name;

        if (rolesSelect) rolesSelect.appendChild(option.cloneNode(true));
        if (editRolesSelect) editRolesSelect.appendChild(option.cloneNode(true));
    });
}

async function handleAddUser(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const data = {
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        username: formData.get('username'),
        password: formData.get('password'),
        roles: Array.from(formData.getAll('roles')).map(id => ({ id: parseInt(id) }))
    };

    try {
        const newUser = await apiPost('/admin/users', data);

        allUsers.push(newUser);
        renderUsersTable();
        resetAddUserForm();
        showNotification('Пользователь успешно добавлен', 'success');

        const usersTab = document.querySelector('button[data-bs-target="#users-tab"]');
        if (usersTab) {
            new bootstrap.Tab(usersTab).show();
        }
    } catch (error) {
        console.error('Add user error:', error);
        if (error.response) {
            try {
                const errors = await error.response.json();
                showFormErrors('addUser', errors);
            } catch (e) {
                showNotification('Ошибка при добавлении пользователя', 'danger');
            }
        } else {
            showNotification('Ошибка при добавлении пользователя', 'danger');
        }
    }
}

async function handleEditUser(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const data = {
        id: parseInt(formData.get('id')),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        username: formData.get('username'),
        password: formData.get('password'),
        roles: Array.from(formData.getAll('roles')).map(id => ({ id: parseInt(id) }))
    };

    try {
        const updatedUser = await apiPut('/admin/users/' + data.id, data);

        const index = allUsers.findIndex(u => u.id === data.id);
        if (index !== -1) {
            allUsers[index] = updatedUser;
        }

        renderUsersTable();
        const modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
        if (modal) modal.hide();
        showNotification('Пользователь успешно обновлен', 'success');
    } catch (error) {
        console.error('Edit user error:', error);
        if (error.response) {
            try {
                const errors = await error.response.json();
                showFormErrors('edit', errors);
            } catch (e) {
                showNotification('Ошибка при обновлении пользователя', 'danger');
            }
        } else {
            showNotification('Ошибка при обновлении пользователя', 'danger');
        }
    }
}

async function handleDeleteUser(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = parseInt(formData.get('id'));

    try {
        await apiDelete('/admin/users/' + userId);

        allUsers = allUsers.filter(u => u.id !== userId);
        renderUsersTable();
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
        if (modal) modal.hide();
        showNotification('Пользователь успешно удален', 'success');
    } catch (error) {
        console.error('Delete user error:', error);
        showNotification('Ошибка при удалении пользователя', 'danger');
    }
}

async function handleLogout(event) {
    event.preventDefault();
    if (!confirm('Вы уверены, что хотите выйти из системы?')) {
        return;
    }
    try {
        const response = await fetch('/logout', {
            method: 'POST',
            headers: getCSRFHeader(),
            credentials: 'include'
        });

        if (response.ok) {
            window.location.href = '/login?logout';
        } else {
            throw new Error('Logout failed with status: ' + response.status);
        }

    } catch (error) {
        console.error('Logout error:', error);
        window.location.href = '/login?logout';
    }
}

function fillEditForm(user) {
    document.getElementById('editUserId').value = user.id;
    document.getElementById('editFirstName').value = user.firstName;
    document.getElementById('editLastName').value = user.lastName || '';
    document.getElementById('editAge').value = user.age;
    document.getElementById('editUsername').value = user.username;
    document.getElementById('editPassword').value = '';

    const rolesSelect = document.getElementById('editRoles');
    if (rolesSelect) {
        Array.from(rolesSelect.options).forEach(option => {
            option.selected = user.roles.some(role => role.id == option.value);
        });
    }

    resetEditForm();
}

function fillDeleteForm(user) {
    document.getElementById('deleteUserId').value = user.id;
    document.getElementById('deleteUsername').textContent = user.username;
    document.getElementById('deleteFirstName').value = user.firstName;
    document.getElementById('deleteLastName').value = user.lastName || '';
    document.getElementById('deleteAge').value = user.age;
    document.getElementById('deleteUserUsername').value = user.username;
}

function resetAddUserForm() {
    const form = document.getElementById('addUserForm');
    if (form) form.reset();

    const errorsContainer = document.getElementById('addUserErrors');
    const errorsList = document.getElementById('addUserErrorsList');

    if (errorsContainer) errorsContainer.style.display = 'none';
    if (errorsList) errorsList.innerHTML = '';

    const inputs = document.querySelectorAll('#addUserForm input, #addUserForm select');
    inputs.forEach(input => input.classList.remove('is-invalid'));
}

function resetEditForm() {
    const errorsContainer = document.getElementById('editErrors');
    const errorsList = document.getElementById('editErrorsList');

    if (errorsContainer) errorsContainer.style.display = 'none';
    if (errorsList) errorsList.innerHTML = '';

    const inputs = document.querySelectorAll('#editForm input, #editForm select');
    inputs.forEach(input => input.classList.remove('is-invalid'));
}

function showFormErrors(formType, errors) {
    const errorsContainer = document.getElementById(formType + 'Errors');
    const errorsList = document.getElementById(formType + 'ErrorsList');

    if (!errorsContainer || !errorsList) return;

    errorsContainer.style.display = 'block';
    errorsList.innerHTML = '';

    Object.entries(errors).forEach(([field, message]) => {
        const li = document.createElement('li');
        li.className = 'text-danger';
        li.textContent = message;
        errorsList.appendChild(li);

        // Помечаем поле как невалидное
        const input = document.querySelector(`[name="${field}"]`);
        if (input) {
            input.classList.add('is-invalid');
            const errorDiv = input.nextElementSibling;
            if (errorDiv && errorDiv.classList.contains('invalid-feedback')) {
                errorDiv.textContent = message;
            }
        }
    });
}

function showNotification(message, type = 'info') {
    // Создаем уведомление
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alert.style.top = '20px';
    alert.style.right = '20px';
    alert.style.zIndex = '1050';
    alert.style.minWidth = '300px';
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alert);

    setTimeout(() => {
        if (alert.parentNode) {
            bootstrap.Alert.getOrCreateInstance(alert).close();
        }
    }, 5000);
}

function checkCSRFToken() {
    const token = getCSRFToken();
    if (!token) {
        console.warn('CSRF token not found. Some operations may fail.');
    }
    return token;
}

checkCSRFToken();