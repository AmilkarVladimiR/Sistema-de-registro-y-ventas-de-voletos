<script setup>
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import Swal from "sweetalert2";
import L from "leaflet"; // Asegúrate de instalar leaflet: npm install leaflet
import 'leaflet/dist/leaflet.css';

const personas = ref([]);

const nuevaPersona = ref({
  nombre: "",
  apellido: "",
  origen: "",
  destino: "",
  precio: "",
  numeroAsiento: ""
});

const editando = ref(false);
const asientosTotales = 40;
const asientosOcupados = ref([]);

const seleccionarAsiento = (numero) => {
  if (asientosOcupados.value.includes(numero)) {
    Swal.fire("Ocupado", `El asiento ${numero} ya está ocupado.`, "error");
    return;
  }
  nuevaPersona.value.numeroAsiento = numero;
};

const cargarAsientosOcupados = () => {
  asientosOcupados.value = personas.value.map((p) => p.numeroAsiento);
};

const cargarPersonas = async () => {
  const response = await axios.get("http://localhost:8080/personas/traer-personas");
  personas.value = response.data;
  cargarAsientosOcupados();
};

const AgregarPersonas = async () => {
  try {
    if (editando.value) {
      await axios.put(`http://localhost:8080/personas/editar-personas/${nuevaPersona.value.id}`, nuevaPersona.value);
      editando.value = false;
      Swal.fire("Actualizado", "La persona fue actualizada correctamente.", "success");
    } else {
      await axios.post("http://localhost:8080/personas/insertar-personas", nuevaPersona.value);
      Swal.fire("Registrado", "La persona fue registrada correctamente.", "success");
    }

    await cargarPersonas();
    nuevaPersona.value = {
      nombre: "",
      apellido: "",
      origen: "",
      destino: "",
      precio: "",
      numeroAsiento: ""
    };
  } catch (error) {
    Swal.fire("Error", error.response?.data || "Ocurrió un error", "error");
  }
};

const editarPersona = async (persona) => {
  const result = await Swal.fire({
    title: "¿Deseas editar esta persona?",
    text: "Se cargará la información en el formulario.",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Sí, editar",
    cancelButtonText: "Cancelar"
  });

  if (result.isConfirmed) {
    Object.assign(nuevaPersona.value, persona);
    editando.value = true;
    Swal.fire("Listo", "Puedes modificar los datos y luego guardar.", "info");
  }
};

const actualizarRuta = (event) => {
  const rutaSeleccionada = event.target.value;

  if (rutaSeleccionada === "tlaxiaco-mexico") {
    nuevaPersona.value.origen = "Tlaxiaco";
    nuevaPersona.value.destino = "México";
    nuevaPersona.value.precio = "250";
  } else if (rutaSeleccionada === "mexico-tlaxiaco") {
    nuevaPersona.value.origen = "México";
    nuevaPersona.value.destino = "Tlaxiaco";
    nuevaPersona.value.precio = "250";
  }
};



const eliminarPersona = async (id) => {
  const result = await Swal.fire({
    title: "¿Estás seguro?",
    text: "¡Esta acción eliminará a la persona permanentemente!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  });

  if (result.isConfirmed) {
    await axios.delete(`http://localhost:8080/personas/eliminar-personas/${id}`);
    await cargarPersonas();
    Swal.fire("Eliminado", "La persona ha sido eliminada.", "success");
  }
};

watch(personas, cargarAsientosOcupados);
onMounted(cargarPersonas);

const initMapaRuta = () => {
  const map = L.map("map").setView([17.2686, -97.6801], 6);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map);

  const tlaxiaco = [17.2686, -97.6801];
  const cdmx = [19.4326, -99.1332];

  L.marker(tlaxiaco).addTo(map).bindPopup("Tlaxiaco").openPopup();
  L.marker(cdmx).addTo(map).bindPopup("CDMX");

  L.polyline([tlaxiaco, cdmx], { color: "blue", weight: 4 }).addTo(map);

  // Nueva línea: CDMX a Puebla
  const puebla = [19.0413, -98.2062];
  L.marker(puebla).addTo(map).bindPopup("Puebla");
  L.polyline([cdmx, puebla], { color: "green", weight: 4 }).addTo(map);

  // Nueva línea: Puebla a Oaxaca
  const oaxaca = [17.0732, -96.7266];
  L.marker(oaxaca).addTo(map).bindPopup("Oaxaca");
  L.polyline([puebla, oaxaca], { color: "purple", weight: 4 }).addTo(map);
};

onMounted(() => {
  cargarPersonas();
  initMapaRuta();
});
</script>

<template>
  <div class="container my-5">
    <div class="card shadow-lg p-4 gradient-bg">
      <div class="text-center mb-4">
        <img
          src="https://www.marvelustravel.com/rutas/mexico/imagenes/ado-occ/occ/occ.jpg"
          class="img-fluid rounded border border-3 border-primary"
          alt="Bus"
          style="max-height: 200px"
        />
        <h2 class="mt-3 text-title">Gestión de Pasajeros - OCC</h2>
      </div>

      <div class="row">

<!-- Selección de Ruta con Estilos Mejorados -->
<div class="col-12 mb-4">
  <div class="card border-0 shadow-sm p-3 bg-light">
    <h3 class="text-primary mb-3 fw-bold text-center">Seleccionar Ruta</h3>
    <div class="mb-2">
      <label for="ruta" class="form-label fw-semibold">Ruta disponible</label>
      <select id="ruta" class="form-select border-primary" @change="actualizarRuta($event)">
        <option disabled selected value="">Selecciona una ruta</option>
        <option value="tlaxiaco-mexico">🚌 Tlaxiaco - México</option>
        
      </select>
    </div>
  </div>
</div>



        <!-- Formulario -->
        <div class="col-md-6 mb-4">
          <div class="card p-3 border-0 shadow-sm h-100 card-form">
<h3 class="text-xl font-bold text-center mb-2 text-blue-700">Formulario de Registro</h3>
            <form @submit.prevent="AgregarPersonas">
              <div class="grid grid-cols-4 gap-4 mb-6">
                <label class="form-label">Nombre</label>
                <input v-model="nuevaPersona.nombre" class="form-control" required />
              </div>
              <div class="mb-2">
                <label class="form-label">Apellido</label>
                <input v-model="nuevaPersona.apellido" class="form-control" required />
              </div>
              <div class="mb-2">
                <label class="form-label">Origen</label>
                <input v-model="nuevaPersona.origen" class="form-control" required />
              </div>
              <div class="mb-2">
                <label class="form-label">Destino</label>
                <input v-model="nuevaPersona.destino" class="form-control" required />
              </div>
              <div class="mb-2">
                <label class="form-label">Precio</label>
                <input v-model="nuevaPersona.precio" type="number" class="form-control" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Asiento Seleccionado</label>
                <input v-model="nuevaPersona.numeroAsiento" class="form-control" readonly />
              </div>
              <button type="submit" class="btn btn-primary w-100 gradient-btn">
                {{ editando ? "Actualizar" : "Agregar" }} Pasajero
              </button>
            </form>
          </div>
        </div>

        <!-- Asientos -->
        <div class="col-md-6 mb-4">
          <div class="card p-3 border-0 shadow-sm h-100 card-asientos">
            <h3 class="text-xl font-bold text-center mb-2 text-blue-700">Seleccionar Asiento</h3>
            <div class="seat-grid">
              <button
                v-for="num in asientosTotales"
                :key="num"
                :class="[
                  'seat-btn',
                  nuevaPersona.numeroAsiento === num ? 'selected' : '',
                  asientosOcupados.includes(num) ? 'occupied' : ''
                ]"
                :disabled="asientosOcupados.includes(num)"
                @click="seleccionarAsiento(num)"
              >
                {{ num }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Tabla -->
      <div class="table-responsive mb-4">
       <h3 class="text-xl font-bold text-center mb-2 text-blue-700">Lista de Pasajeros</h3>
        <table class="table table-hover table-bordered align-middle">
          <thead class="table-dark">
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Apellido</th>
              <th>Origen</th>
              <th>Destino</th>
              <th>Precio</th>
              <th>Asiento</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="persona in personas" :key="persona.id">
              <td>{{ persona.id }}</td>
              <td>{{ persona.nombre }}</td>
              <td>{{ persona.apellido }}</td>
              <td>{{ persona.origen }}</td>
              <td>{{ persona.destino }}</td>
              <td>{{ persona.precio }}</td>
              <td>{{ persona.numeroAsiento }}</td>
              <td>
                <button @click="editarPersona(persona)" class="btn btn-warning btn-sm me-2">✏️</button>
                <button @click="eliminarPersona(persona.id)" class="btn btn-danger btn-sm">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mapa -->
      <div>
        <h3 class="text-xl font-bold text-center mb-2 text-blue-700">Ruta del Autobús</h3>
        <div id="map" class="map-container"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  font-family: 'Segoe UI', sans-serif;
}

.gradient-bg {
  background: linear-gradient(to bottom right, #6a801e, #ff4d4d);
  border-radius: 15px;
}

.gradient-btn {
  background: linear-gradient(45deg, #c8a300, #1e88e5);
  color: white;
  border: none;
  font-weight: bold;
}

.gradient-btn:hover {
  opacity: 0.9;
}

.text-title {
  color: #070707;
  font-weight: bold;
  font-size: 2rem;
}

.text-subtitle {
  color: #0d0e0d;
  font-weight: 600;
}

.card-form {
  background-color: rgba(255, 255, 255, 0.9);
  border-left: 5px solid #ff4d4d;
  border-radius: 10px;
}

.card-asientos {
  background-color: rgba(240, 248, 255, 0.95);
  border-left: 5px solid #004dff;
  border-radius: 10px;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.seat-btn {
  padding: 10px;
  border-radius: 8px;
  border: none;
  font-weight: bold;
  background-color: #1e88e5;
  color: white;
  transition: 0.3s;
}

.seat-btn:hover {
  background-color: #1565c0;
}

.seat-btn.occupied {
  background-color: #e53935;
  cursor: not-allowed;
}

.seat-btn.selected {
  background-color: #a07b43;
}

.map-container {
  height: 400px;
  width: 100%;
  margin-top: 10px;
  border-radius: 10px;
  overflow: hidden;
  border: 3px solid #1e88e5;
}

.form-select.border-primary {
  border: 2px solid #0d6efd;
  border-radius: 8px;
  transition: box-shadow 0.3s ease;
}

.form-select.border-primary:focus {
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

</style>