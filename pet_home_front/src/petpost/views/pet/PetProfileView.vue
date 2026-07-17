<script setup>

import PetProfile
from "@/petpost/components/pet/PetProfile.vue";

import { usePetStore }
from "@/petpost/stores/petStore";

import '@/petpost/assets/petpost-theme.scss'

const petStore =
usePetStore();

</script>


<template>

<div class="container pet-profile-page petpost-photo-page">

    <PetProfile

        :pet="petStore.pets[0]"

    />

</div>

</template>